# TaskTracker
A JavaFX to-do list

## Description
This application provides the user with a clean graphical interface for managing their day-to-day tasks.

## Usage
The window consists of two primary components: the table of current tasks and the editing panel. In order to add a task, the user simply provides a description of the task, its deadline, and a project name and color-code (used for grouping multiple tasks) in the designated fields, then clicks `Add`. All fields must be filled in order to add an item and none of the fields can contain an `@` sign as this is used by the program for file input/output.

![image](https://user-images.githubusercontent.com/100661064/167276150-cccfa47e-5e14-4649-9357-54a61c12d4d3.png)

In order to delete an already existing task, the user clicks on the row listing it. Once it is highlighted, the user can then simply press the `Delete` key and it will be removed from the list.

To edit an existing task, click the row you want to edit and then click `Edit`; the editing panel will autofill with the input for that task and it can then be changed. To save these changes, click `Save`.

Whatever tasks are currently being displayed in the table will be saved automatically upon exiting out of the application.

The table is also capable of sorting tasks based upon their description, deadline, or project name values simply by clicking the top header of that respective column.

## Implementation
The application was developed using the JavaFX GUI library and is therefore required to run the application. In order to save the current to-do list, the application also requires that a text file, `Record.txt` be included in the program's directory. An empty text file by this name has been included in this repository for convenience.
