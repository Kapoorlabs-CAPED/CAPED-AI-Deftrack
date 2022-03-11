dir = getDirectory("");
title = getTitle();
selectWindow(title);

run("Split Channels");

selectWindow("C2-"+ title);
saveAs("tiff",dir+"C2-"+ title);
run("Duplicate...", "duplicate");
run("Gaussian Blur...", "sigma=2 stack");

setOption("BlackBackground", false);
run("Make Binary", "method=Default background=Default calculate");
run("Fill Holes", "stack");
run("Erode", "stack");
saveAs("tiff",dir+"Binary_"+ title);
