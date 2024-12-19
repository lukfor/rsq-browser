# Getting Started

<br>
## Minimum input

To use the wizard (our main functionality) you must select at least two inputs:

- One of the populations of the drop-down list
- already genotyped -> yes/no (drop-down list)
<br>
<br>
## Detailed Description of the Input Mask  
<p style="margin-bottom: 20px;">  </p>  

#### What is the population of your study?

You are required to choose exactly one populations for each submit (direct cross-population evaluation currently not supported).

However, if you are interested in multiple populations simultaneously, we recommend to make separate submits per population.

At present, the following populations are available:

- African
- European
- Finnish
- Hispanic/Latino
<p style="margin-bottom: 20px;">  </p>  
#### Have you already genotyped your data?

If you already genotyped your data you should choose "Yes" to restrict the analysis to the most suitable genotyping array/chip.

Otherwise, we recommend to select "No" to obtain results for all genotyping arrays.

If you choose "Yes" you must select one of the chips in the dropdown menu.

At present, reference data for the following genotyping arrays are available:

- Core (Illumina Infinium Core)
- OmniExpress (Illumina Omni Express)
- Omni 2.5M (Infinium Omni 2.5M)
- MEGA (Multi-Ethnic Genotyping Array)

If your array is not in this list, we recommend to use the array, which is most similar with regard to the number of genotyped variants.

Metadata about the genotyping arrays is provided [here](#calculation-of-performance-scores).

#### Which genes are you interested in? (Enter one gene per line)

Here you can optionally add a list of genes of interest. The wizard will then provide imputation quality statistics for each of these genes.

Please enter one gene per line using the official HGNC gene symbol (e.g. LPA for the Lp(a) gene)

#### Which SNPs are you interested in? (Enter one rs-number per line)

Here you can optionally add a list of SNPs of interest. The wizard will then provide imputation quality statistics for each of these SNPs.

Please note that currently only bi-allelic single nucleotide variants are represented in the reference data.

#### Which Polygenic Risk Scores are you interested in? (Enter one PGS Catalog ID per line)

Here you can optionally add a list of PGS IDs of interest. The wizard will then provide imputation quality statistics for each of these PGS. 

Only valid PGS IDs from the PGS Catalog will be accepted.

<br>
## Valid Example Input

![](images/ex_interface.PNG)

<br>
<br>

## Overview Dashboard

<br>
<br>

## Description of Genotyping Arrays

| Array         | Number of Variants | African   | Hispanic/Latino | European  | Finnish   |
|---------------|--------------------|-----------|------------------|-----------|-----------|
| Omni 2.5M     | 2,381,000          | 2,132,501 | 2,330,998        | 2,330,998 | 2,264,709 |
| MEGA          | 1,780,000          | 1,415,237 | 1,759,171        | 1,759,171 | 1,676,050 |
| OmniExpress   | 710,000            | 680,234   | 706,652          | 706,652   | 698,865   |
| Core          | 307,000            | 266,727   | 288,599          | 288,599   | 302,423   |

