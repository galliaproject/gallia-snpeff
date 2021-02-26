package galliaexample.snpeff

import scala.util.chaining._ // for .pipe
import gallia._

// ===========================================================================
object SnpEffOutputParsing {    

  // borrowed from snpEff's "cancer.ann.vcf" example (https://github.com/pcingola/SnpEff/blob/v4.3t/examples/cancer.ann.vcf)
  val TestValue = "AC=1;ANN=G|start_lost|HIGH|OR4F5|ENSG00000186092|transcript|ENST00000335137|protein_coding|1/1|c.1A>G|p.Met1?|1/918|1/918|1/305||,G-C|start_lost|HIGH|OR4F5|ENSG00000186092|transcript|ENST00000335137|protein_coding|1/1|c.1A>G|p.Leu1?|1/918|1/918|1/305||WARNING_REF_DOES_NOT_MATCH_GENOME,C|initiator_codon_variant|LOW|OR4F5|ENSG00000186092|transcript|ENST00000335137|protein_coding|1/1|c.1A>C|p.Met1?|1/918|1/918|1/305||;LOF=(OR4F5|ENSG00000186092|1|1.00)"

  // ===========================================================================
  def main(args: Array[String]): Unit = {
    TestValue
      .readContent()

        // emulate VCF JSON representation (as in https://github.com/galliaproject/gallia-clinvar/blob/init/src/main/scala/galliaexample/vcf/Vcf.scala#L10)
        .rename(_content ~> 'INFO)

        // emulate case where more than one gene is affected (more interesting)
        .transform('INFO).using(_ + ",(OR4F5b|ENSG00000186092b|2|0.5)")
        
        // actual processing
        .pipe(processSnpEff)
        
      .printJson()
  }

  // ===========================================================================
  def processSnpEff: HeadO => HeadO =
    _

      // ---------------------------------------------------------------------------
      // "untuplify" -> see https://github.com/galliaproject/gallia-core#why-does-the-terminology-sometimes-sound-funny-or-full-on-neological
      .untuplify2a('INFO)
        .withSplitters( // see VCF specification (eg "RS=1235;ALLELEID=...")
            entriesSplitter = ";",
              entrySplitter = "=")
          .asNewKeys('AC, 'ANN, 'LOF, 'NMD)
          
      // ===========================================================================
      .transformObject('INFO).using {
        _
        
            .convert('AC).toInt
            
            // ---------------------------------------------------------------------------
            .pipe(process('LOF))
          //.pipe(process('NMD)) // works the same way for Nonsense Mediated Decay (not in example though)
    
            // ===========================================================================
            // process ANN
      
           .untuplify1b('ANN)
             .withSplitters(
                 arraySplitter   = ",",
                 entriesSplitter = "|")
               .asNewKeys(
                  // as per meta-information line
                  'Allele,
                  'Annotation,
                  'Annotation_Impact,
                  'Gene_Name,
                  'Gene_ID,
                  'Feature_Type,
                  'Feature_ID,
                  'Transcript_BioType,
                  'Rank,
                  "HGVS.c",
                  "HGVS.p",
                  "cDNA.pos / cDNA.length",
                  "CDS.pos / CDS.length",
                  "AA.pos / AA.length",
                  'Distance,
                  "ERRORS / WARNINGS / INFO" )
                  
            // ---------------------------------------------------------------------------              
            .removeRecursivelyIfValue("") // eg "Distance"        

            // ---------------------------------------------------------------------------  
            .transformObjects('ANN).using {
              _

                // ---------------------------------------------------------------------------
                                                          .pipe(processSerializedRatio('Rank)('value, 'total))        
                .rename("cDNA.pos / cDNA.length" ~> 'cDNA).pipe(processSerializedRatio('cDNA)('pos,   'length))
                .rename("CDS.pos / CDS.length"   ~> 'CDS ).pipe(processSerializedRatio('CDS) ('pos,   'length))
                .rename("AA.pos / AA.length"     ~> 'AA  ).pipe(processSerializedRatio('AA)  ('pos,   'length))
    
                // ---------------------------------------------------------------------------            
                // not strictly necessary but showcases nesting
                .nest(
                    "HGVS.c" ~> 'c,
                    "HGVS.p" ~> 'p)
                  .under('HGVS)

                // ---------------------------------------------------------------------------
                // because come on...
                .rename("ERRORS / WARNINGS / INFO" ~> 'ERRORS_WARNINGS_INFO) } }

  // ===========================================================================
  def process(target: Key /* LOF or NMD */): HeadO => HeadO =    
    _   .split(target).by {
          _.tail.init.split("\\),\\(") }
       
        // "untuplify" -> see https://github.com/galliaproject/gallia-core#why-does-the-terminology-sometimes-sound-funny-or-full-on-neological
        .untuplify1a(target)
          .withSplitter("|")
            .asNewKeys(
              // as per meta-information line
              'Gene_Name,
              'Gene_ID,
              'Number_of_transcripts_in_gene,
              'Percent_of_transcripts_affected)

      // ---------------------------------------------------------------------------
     .transformObjects(target).using {
        _ .convert('Number_of_transcripts_in_gene  ).toInt    
          .convert('Percent_of_transcripts_affected).toDouble }
  
  // ===========================================================================
  /** eg field "cDNA.pos / cDNA.length" with value "3/14" is actually made up of two new fields: "pos" and "length", 
   *  with values 3 and 14 respectively, and that are to be nested under the new name of "cDNA" */  
  def processSerializedRatio(target: KeyW)(newFieldName1: Key, newFieldName2: Key): HeadS => HeadS =
    _ 
        // "untuplify" -> see https://github.com/galliaproject/gallia-core#why-does-the-terminology-sometimes-sound-funny-or-full-on-neological
       .untuplify1a(target.value /* eg AA */)
         .withSplitter("/")
           .asNewKeys(
             newFieldName1,
             newFieldName2)

      .convert( // eg pos and length
          target.value |> newFieldName1,
          target.value |> newFieldName2)
        .toInt
}

// ===========================================================================

