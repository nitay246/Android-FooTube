the work flow
we began by creating the login and signup screens in order to do that we first created the design
in the relevant xml and then implemented the logic in the java classes.
after that we split up some of us worked on the homepage and the rest worked on the watch screen we
found out the hard way that it is better to use urls to play video then to store them locally.
next we created the edit page and the upload page
in order to save a local copy of videos and users that resets each run we used the singleton design
pattern it worked pretty well, then we needed to add s functioning search bar we used the singleton
to look for a video with the same name and returned it.
last but not least we needed to implement dark mode and that was quiet the hassle we ahd to create
colors for the day and colors for night same with a styles file,
then we had to go over the xml files and make them implement styles the final part was pretty hard
coding the actual switch in the classes after a lot of infinite dark mode light mode loops we got it