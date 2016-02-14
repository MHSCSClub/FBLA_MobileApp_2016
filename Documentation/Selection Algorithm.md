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

We want to give users a reasonable amount of feedback in a reasonable time frame. We do this by mapping views and time passed to a value `p`, the score of the picture. The picture with the highest score will be shown first. Both views and time contribute a subscore which we add together. `p` ranges between 0 and 100. Views contributes a maximum of `70` subscore while time contributes a maximum of `30`.

#### Views

Currently we aim for around 11-13 views on any given picture. To achieve this goal, we set drop off points. **10** is the magic number for views. At around 10 views, the score from views drops off drastically. 

Our current view function looks like this:

![Graph](http://i.imgur.com/Et3Wrbd.png)

This is given by a piecewise function:
+ Views less than or equal to 10: `p = (30 * Ln[11 - v])/Ln[11] + 40`
+ Views greater than 10: `p = 30/(x - 10)`

#### Time

Time is a lot easier. The score increases as time passes reaching a maximum cap of `30` at 10 hours.

![Graph](http://i.imgur.com/xMs6iHc.png)

Another piecewise function:
+ Time less than 10h: `p = 3t`
+ Time greater than 10h `p = 30`

We determine a final score by adding subscores. Whatever has the higher score gets shown first.
