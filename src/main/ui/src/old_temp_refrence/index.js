import React from 'react';
import { createRoot } from 'react-dom/client';
import './index.css';
import AppRouter from "./AppRouter";
import 'bootstrap/dist/css/bootstrap.min.css';

const inventory = [
    {
        "Category": "Sprocket",
        "Id": "S100",
        "Item": "Bicycle Sprocket",
        "Description": "Industrial strength Sprocket useful for bike gears. Highly efficient gear changes",
        "Count": 234,
        "Cost": 9.99
    },
    {
        "Category": "Sprocket",
        "Id": "S101",
        "Item": "Electric Motor Bike Sprocket",
        "Description": "Smooth gear changes with this aluminum alloy sprocket. Comes in Silver, gold or black",
        "Count": 921,
        "Cost": 16.99
    },
    {
        "Category": "Sprocket",
        "Id": "S102",
        "Item": "Rocket Sprocket",
        "Description": "Use this baby to control your rocket ship. Shifts gears into hyperspace without any though",
        "Count": 1230,
        "Cost": 5099.99
    },
    {
        "Category": "Sprocket",
        "Id": "S103",
        "Item": "Jet Sprocket",
        "Description": "Go into super fast mode quickly and without any energy at all. Comes in gold, blue, or yellow.",
        "Count": 1590,
        "Cost": 4999.99
    },
    {
        "Category": "Sprocket",
        "Id": "S104",
        "Item": "Boat Sprocket",
        "Description": "Place this sprocket in the right place on your boat and it will float better. We are just not sure where to put it. ",
        "Count": 199,
        "Cost": 19.99
    },
    {
        "Category": "Cog",
        "Id": "C100",
        "Item": "Bicycle Cog",
        "Description": "Use this special bike cog to do cog things on a bike. Its so useful we don't even list the uses",
        "Count": 200,
        "Cost": 12.99
    },
    {
        "Category": "Cog",
        "Id": "C101",
        "Item": "Rocket Cog",
        "Description": "Space this Cog in a special place on your rocket and you can do amazing things. Comes in Blue only",
        "Count": 1200,
        "Cost": 2999.99
    },
    {
        "Category": "Cog",
        "Id": "C102",
        "Item": "Jet Cog",
        "Description": "We are not sure why you would want a cog on a jet but hey we got them. Maybe you can figure out something to do with them.",
        "Count": 9999,
        "Cost": 199.99
    }
]

const container = document.getElementById('root');
const root = createRoot(container);

root.render(
  <React.StrictMode>
    <AppRouter inventory={inventory}/>
  </React.StrictMode>,
);