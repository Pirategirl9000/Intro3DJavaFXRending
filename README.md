# Java 3D Shooter Game
### Created by: *Braedyn (Violet) French*
___
## How does the camera movement work?
___
### The camera uses trigonometry executed on the x & z axis to determine a vector of motion based on the x-tilt angle of the camera
### This vector of motion allows us to determine how much the camera should move along each axis allowing for movement controls to be relative to the camera's direction
### This motion vector is defined by <x, z> = <SPEED * sin(angle), SPEED * cos(angle)>
### Usually motion vectors are defined as x = cos(angle) but because we are attempting trigonometry on the x and z axis the trigonometry changes
### We can determine that the z-axis associates with cos since at angle=0 the camera is facing the positive z-axis so we use a trig function that has f(0) = 1
#
### We use this same logic for determing the bullet's motion vector except we also include the y-tilt just for determining the y-velocity
### This is because the bullet is traversing all three axis, and it's movement is based on the angle of the camera
### The logic is the same but the trig changes a little bit since we need to use a different angle to determine the y part of the motion vector
### The y part uses basic trig for the most part but using the y-tilt angle instead of the x-tilt angle.
### The y portion of the vector needs negation however since the y value increases as you go down because of how programs typically render the y-axis
___
## Class Breakdow
___
## Main
### The Main class serves as the driver code for the program and handles instantiation of the 3D environment as well as it's child nodes
### The class also handles the basic gameloop through an AnimationTimer which pulses at a rate of 60hz meaning the game's physics are updated at a rate of 60FPS
### The AnimationTimer is at the center of the game as it is what drives the physics
#
### The Main class also handles hit collision by checking the bounding boxes of different nodes to see if they overlap
#
### The different nodes created are all held within "Group root" in the Main class which is then passed to the scene to create the environment
### All new nodes are to be pushed to root to allow it to be rendered on stage
---
## 