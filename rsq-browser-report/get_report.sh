#!/bin/bash

quarto preview report.Rmd --to html --no-watch-inputs --no-browse
#Rscript -e "rmarkdown::render('report.Rmd')"
#Rscript -e "rmarkdown::render('example.Rmd',params=list(args = myarg))"
