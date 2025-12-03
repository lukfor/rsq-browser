
library(data.table)
library(dplyr)
library(tidyr)

setwd("//wsl.localhost/Ubuntu-22.04/home/flo/projects/rsq-browser/local_files/data_local/")


### load merged result files
merged_res <- fread("generate_PGS_statistics/results_MARS/EUR_with_small_error/merged/EUR_merged.txt")

### load in PGS metadata to get total nr of PGS variants
pgs_metadata <- fread("PGS_all/pgs_all_metadata_scores.csv")



merged_res <- merged_res %>%
  rename(identifier = `RSQ Column`, PGS = `PGS Column`) %>%
  select(-Sum, -Invalid)

### Variants -> total nr. of variants - invalid (due to e.g. wrong chromosome or sex chromosome)
### Found -> perfect matches -> relevant
### Not found -> e.g. allele mismatch or missing (but does not capture all missing!)

summary_df <- merged_res %>%
  group_by(identifier, PGS) %>%
  summarise(nr_var_valid = sum(Variants, na.rm = T),
            nr_indels = sum(Indels, na.rm = T),
            nr_matches = sum(Found, na.rm = T),
            nr_hq_matches = sum(Good_Quality, na.rm = T),
            ultra_rare.mean_r2 = sum(R2_a, na.rm = T)/sum(N_a, na.rm = T),
            ultra_rare.n = sum(N_a, na.rm = T),
            rare.mean_r2 = sum(R2_b, na.rm = T)/sum(N_b, na.rm = T),
            rare.n = sum(N_b, na.rm = T),
            low_freq.mean_r2 = sum(R2_c, na.rm = T)/sum(N_c, na.rm = T),
            low_freq.n = sum(N_c, na.rm = T),
            common.mean_r2 = sum(R2_d, na.rm = T)/sum(N_d, na.rm = T),
            common.n = sum(N_d, na.rm = T),
            ) %>%
    ## split identifier to get columns with array and ref_panel names
    separate_wider_delim(cols = identifier, ## column to separate/split
                         delim = "_", ## delimiter to separate/split on
                         names = c("genotyping_array", "ref_panel"),  ## names of output columns from split results
                         cols_remove = FALSE)




### only keep columns of interest in pgs metadata
pgs_metadata <- pgs_metadata %>%
  select(`Polygenic Score (PGS) ID`, `Reported Trait`, `Number of Variants`) %>%
  rename(PGS = `Polygenic Score (PGS) ID`, nr_var_total = `Number of Variants`, reported_trait = `Reported Trait`)


### merge summary df with pgs metadata
summary_df <- merge(summary_df, pgs_metadata, by = "PGS")


### bring into optimal format for index.qmd code
summary_df <- summary_df %>%
  mutate(nr_invalid = nr_var_total - nr_var_valid,
         nr_SNVs = nr_var_total - nr_indels - nr_invalid) %>%
  # select(PGS, reported_trait, genotyping_array, ref_panel, nr_var_total, nr_indels, nr_invalid, nr_SNVs, nr_matches, nr_hq_matches,
  #        ultra_rare.mean_r2, ultra_rare.n, rare.mean_r2, rare.n, low_freq.mean_r2, low_freq.n, common.mean_r2, common.n)
  select(PGS, genotyping_array, ref_panel, nr_var_total, nr_indels, nr_invalid, nr_SNVs, nr_matches, nr_hq_matches,
         ultra_rare.mean_r2, ultra_rare.n, rare.mean_r2, rare.n, low_freq.mean_r2, low_freq.n, common.mean_r2, common.n)


### save result to csv file
write.table(summary_df, file = "mlof.bi.snv.tab.gz.pgs.csv", quote = F, row.names = F, sep = "\t")













