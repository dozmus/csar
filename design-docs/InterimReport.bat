@echo off
echo Generating PDF...
pdflatex "Interim Report & Risk Assessment"
biber "Interim Report & Risk Assessment"
pdflatex "Interim Report & Risk Assessment"
echo Cleaning temporary files...
del "Interim Report & Risk Assessment.log"
del "Interim Report & Risk Assessment.out"
del "Interim Report & Risk Assessment.aux"
del "Interim Report & Risk Assessment.toc"
del "Interim Report & Risk Assessment.bbl"
del "Interim Report & Risk Assessment.bcf"
del "Interim Report & Risk Assessment.blg"
del "Interim Report & Risk Assessment.run.xml"
echo Done.
