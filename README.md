# PizzaDronz
### About 
- I created this app for my 3rd year Informatics Large Project
- I received a mark of 71.5% on this project
### Project Specifications
- implement an algorithm to control the flight of the drone
- algorithm must be able to find efficient flightpath while avoiding no-fly-zones
- data for the pizza deliveries is fetched from REST-server in json format
### Approach
- implemented a basic geometry engine so that the drone has collision detection
- implemented my own take on the floodfill algorithm using the functions of the geometry the geometry engine. This is based on the drone trying to take optimistic shortest path and then recalculating the next most optimistic path once a collision is detected
- the use of this algorithm means that the app is very lightweight and efficient. The drone does not need to load in data of the entire map, only the coordinates of the vertices of the obstacles. The geometry engine does the rest of the work.
