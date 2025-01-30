# Install and load the package "effectsize"
install.packages("effsize")
library(effsize)

# Set the names of the directories
datadir <- "data"
resdir <- "results"

# Get the subfolder
objdirs <- list.files(datadir)

# For each folder, list the files and compute the effect size test

for(d in 1:length(objdirs)){
	directory <- objdirs[d]
	csvfiles <- list.files(paste(datadir,"/",directory,sep=""))
	for(f in 1:length(csvfiles)){
		mycsvfile <- csvfiles[f]
		if(grepl(".csv",mycsvfile)){
			data <- read.csv(paste(datadir,"/",directory,"/",mycsvfile,sep=""))
			# Set the output file name
			sink(paste(resdir,"/",substr(mycsvfile,0,(nchar(mycsvfile)-4)),".txt",sep=""))
	
			# Call the test for each pair of algorithms
			algorithms <- colnames(data)
			for(i in 2:ncol(data)){
				for(j in 2:ncol(data)){
					if(i!=j){
						print(paste(algorithms[i], "vs", algorithms[j]));
						res <- cliff.delta(data[,i],data[,j])
						print(res)
						writeLines("\n\n")
					}
				}
			}
			# Close the output
			sink()
		}
	}
}