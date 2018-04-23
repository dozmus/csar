@echo off
echo Generating PDF...
del "Report.pdf"
pdflatex "Report"
biber "Report"
pdflatex "Report"
echo Cleaning temporary files...
del "Report.log"
del "Report.out"
del "Report.aux"
del "Report.toc"
del "Report.bbl"
del "Report.bcf"
del "Report.blg"
del "Report.run.xml"
echo Done.
