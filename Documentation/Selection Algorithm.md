# Selection Algorithm

One challenge that needs to be addressed is which picture to select first for a user to evaluate. Three factors need to be considered (listed in order of importance):
+ Views: how many users have already viewed (and given feedback!) on the picture
+ Time: how long since the time of picture creation
+ Distance: how far away from the original destination of picture

The goal is for anyone that submits a picture get a reasonable amount of feedback in a reasonable time frame from people who he/she is reasonable familar with.

Picture selection for the end user is done in two stages: **filtering** and **scoring**.

## Filtering

This is the first stage in determining which picture to display. It gets rid of extraneous pictures: those that are too old, already frequently commented on or too far.

Current filters:
+ `views > 15`
+ `time > 5 days`
+ `dist > 10 miles`

These are default values and should be able for the end user to change in the settings.

## Scoring

After filtering out unwanted pictures, the next step is to score them. Here we focus with an emphasis of views over time. Distance is not considered in the scoring as there is a tenuous relationship between distance and aqauintance past a certain limit.
