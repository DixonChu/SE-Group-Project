# Welcome to the Welsh Vocabulary Tutor v 1.0

## Instruction how to install the application

## Getting Started
These instruction will get you a copy of the project up and running on your local machine for development and testing purpose. See deployment for notes on how to deploy the project on live stream.
## Prerequisites
`javafx-sdk-13.0.1`
`javafx scene builder`
`IntelliJ IDEA`
## Installing 
1. Clone https from gitlab into a new folder `git clone https://gitlab.dcs.aber.ac.uk/mab152/welshapp.git` using git bash.
1. Open the project in IntelliJ IDEA.
1. Once the project has opened in Intellij IDEA, go to `File -> Settings -> Appereance and behaviour -> Path Variable` add the library of _javafx-sdk-13.0.1_ 
1. Name the variables `PATH_TO_FX` and the value is the path where you stored your javafx-sdk. Click `Apply` after it has successfully added. 
1. Go to `<Run -> Edit Configurations -> Application -> Main>` 
1. At the `<VM options>`, added this line `--module-path ${PATH_TO_FX} --add-modules javafx.controls,javafx.fxml` and click `Apply`
1. Go to `File -> Project Structure -> Project Settings -> Libraries`, in the libraries, add a New Project Library of Java then choose the `lib` file of you __javafx-sdk-13.0.1__ and click `APPLY`.
1. Next go to `File -> Project Structure -> Project Settings -> Modules`, if your *src* folder is not in blue colour, click the *src* folder then under the *Language level*, click the blue folder named Sources in the section *Mark As:* 
1. Same to the *out* folder, but it has to be mark as *Excluded*.
1. Finally within th 'tests' directory choose TestDictionaryFunctionallity
1. Click on junit with right click and choose option 'Show Context Action' and choose JUnit 5.4























