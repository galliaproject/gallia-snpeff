<ins>__Note__</ins>: _This is only intended to showcase processing in Gallia, it is not complete nor thoroughly tested at the moment. Use output at your own risk._

For more information, see gallia-core [documentation](https://github.com/galliaproject/gallia-core/blob/init/README.md#introducing-gallia-a-scala-library-for-data-manipulation), in particular the bioinformatics examples [section](https://github.com/galliaproject/gallia-core/blob/init/README.md#bioinformatics-examples).

<a name="description"></a>
### Description
Uses _Gallia_ [transformations](https://github.com/galliaproject/gallia-snpeff/blob/init/src/main/scala/galliaexample/snpeff/SnpEffOutputParsing.scala#L24)

to turn VCF INFO values such as:

<a name="input"></a>
```plain
AC=1;ANN=G|start_lost|HIGH|OR4F5|ENSG00000186092|transcript|ENST00000335137|protein_coding|1/1|c.1A>G|p.Met1?|1/918|1/918|1/305||,G-C|start_lost|HIGH|OR4F5|ENSG00000186092|transcript|ENST00000335137|protein_coding|1/1|c.1A>G|p.Leu1?|1/918|1/918|1/305||WARNING_REF_DOES_NOT_MATCH_GENOME,C|initiator_codon_variant|LOW|OR4F5|ENSG00000186092|transcript|ENST00000335137|protein_coding|1/1|c.1A>C|p.Met1?|1/918|1/918|1/305||;LOF=(OR4F5|ENSG00000186092|1|1.00)
```

into objects like:

<a name="output"></a>
```json
{
  "AC": 1,
  "LOF": [
    { "Gene_Name": "OR4F5",
      "Gene_ID": "ENSG00000186092",
      "Number_of_transcripts_in_gene": 1,
      "Percent_of_transcripts_affected": 1.0 },
    { "Gene_Name": "OR4F5b",
      "Gene_ID": "ENSG00000186092b",
      "Number_of_transcripts_in_gene": 2,
      "Percent_of_transcripts_affected": 0.5 } ],
  "ANN": [
    {
      "Allele": "G",
      "Annotation": "start_lost",
      "Annotation_Impact": "HIGH",
      "Gene_Name": "OR4F5",
      "Gene_ID": "ENSG00000186092",
      "Feature_Type": "transcript",
      "Feature_ID": "ENST00000335137",
      "Transcript_BioType": "protein_coding",
      "Rank": {
        "value": 1,
        "total": 1 },
      "cDNA": {
        "pos": 1,
        "length": 918 },
      "CDS": {
        "pos": 1,
        "length": 918 },
      "AA": {
        "pos": 1,
        "length": 305 },
      "HGVS": {
        "c": "c.1A>G",
        "p": "p.Met1?" }
    },
    {
      "Allele": "G-C",
      "Annotation": "start_lost",
      "Annotation_Impact": "HIGH",
      "Gene_Name": "OR4F5",
      "Gene_ID": "ENSG00000186092",
      "Feature_Type": "transcript",
      "Feature_ID": "ENST00000335137",
      "Transcript_BioType": "protein_coding",
      "ERRORS_WARNINGS_INFO": "WARNING_REF_DOES_NOT_MATCH_GENOME",
      "Rank": {
        "value": 1,
        "total": 1 },
      "cDNA": {
        "pos": 1,
        "length": 918 },
      "CDS": {
        "pos": 1,
        "length": 918 },
      "AA": {
        "pos": 1,
        "length": 305 },
      "HGVS": {
        "c": "c.1A>G",
        "p": "p.Leu1?" }
    },
    {
      "Allele": "C",
      "Annotation": "initiator_codon_variant",
      "Annotation_Impact": "LOW",
      "Gene_Name": "OR4F5",
      "Gene_ID": "ENSG00000186092",
      "Feature_Type": "transcript",
      "Feature_ID": "ENST00000335137",
      "Transcript_BioType": "protein_coding",
      "Rank": {
        "value": 1,
        "total": 1 },
      "cDNA": {
        "pos": 1,
        "length": 918 },
      "CDS": {
        "pos": 1,
        "length": 918 },
      "AA": {
        "pos": 1,
        "length": 305 },
      "HGVS": {
        "c": "c.1A>C",
        "p": "p.Met1?" }
    }
  ]
}
```

<a name="references"></a>
### SnpEff References
- publication: _"A program for annotating and predicting the effects of single nucleotide polymorphisms, SnpEff: SNPs in the genome of Drosophila melanogaster strain w1118; iso-2; iso-3.", Cingolani P, Platts A, Wang le L, Coon M, Nguyen T, Wang L, Land SJ, Lu X, Ruden DM. Fly (Austin). 2012 Apr-Jun;6(2):80-92. PMID: 22728672_
- website: https://pcingola.github.io/SnpEff/

