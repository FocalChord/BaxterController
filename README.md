# Project: Designing a Mobile Application for the Baxter Robot

This was a 10 week project done by Dinith Wannigama and Nisarag Bhatt in the summer of 2017-2018 under the supervision of Dr. Duleepa Thrimawithana and Andrew Chen at the University of Auckland.

The main objective of this project was to make a mobile android app to control the Baxter Research Robot. This includes being able to control its arms, head and ultimately the mobile base. 

# BaxterController &mdash; the app for Android to control Baxter
This repository contains content for a project to build an android app to control a robot called Baxter.

## About BaxterController

BaxterController is an Android app to control the movement of multiple parts of the [robot produced by Rethink Robotics](http://www.rethinkrobotics.com/baxter/). 

The application's main goal was to let a user interact and control Baxter's arm/head/gripper with a easy to use familiar interface. 

# Installation

This app can either be used on the Baxter robot or the Gazebo simulator on ROS.

## Using Gazebo Simulator with the App

### **Setting up Ubuntu**


For this part, we will require Ubuntu 14.04 for ROS Indigo since the latest version of Ubuntu is not supported. 

A download for Ubuntu 14.04 can be found [here](http://releases.ubuntu.com/14.04/).

One way of installing Ubuntu on your machine could be to dual boot it, however we cannot guarantee this would work since we have only tested it in a virtual machine.

The way we installed Ubuntu was to use a virtual machine, some options are:
- [Virtual Box](https://www.virtualbox.org/)
- [VMware Fusion](https://my.vmware.com/en/web/vmware/info/slug/desktop_end_user_computing/vmware_fusion/10_0) (This was used in this project)

Allocate decent RAM to the virtual machine (4GB Recommended) and give the virtual machine around 20GB (can do more if required).

After installation, make sure you don't update the Ubuntu to the latest version.

### **Setting up ROS Workstation and Gazebo Simulator**

For the second part we are going to be using this [link](http://sdk.rethinkrobotics.com/wiki/Workstation_Setup) to install ROS Indigo. 

Open up Terminal in Ubuntu and go to the link and follow these steps:

- Step 2: Install ROS
- Step 3: Create Baxter Development Workspace
- Step 4: Install Baxter SDK Dependencies
- Step 5: Install Baxter Research Robot SDK
- Step 6: Configure Baxter Communication/ROS Workspace
    - Note that everything you do in this step will be erased so we can go through this after the simulator installation. 
    - However you can fill it out to test that the environment works in step 7.
- Step 7: Verify Environment
    - Go through this step to make sure the ROS environment is working. 

### **Setting up the Socket Server** 

In the repo: There is a folder called Python Socket Server: 

<img width="1658" alt="screen shot 2018-02-12 at 1 41 00 pm" src="https://user-images.githubusercontent.com/31643423/36080796-df391d4e-0ffa-11e8-8f3b-0b528c0e3e66.png">

Copy the contents of it to Ubuntu into the `ros_ws` directory and that is all for this step.



### **Installing Baxter Simulator**

For this third part of the installation we are going to be using this [link](http://sdk.rethinkrobotics.com/wiki/Simulator_Installation) to install the Gazebo Baxter Simulator.

Have Terminal open in Ubuntu and go to the above link and follow these steps:

- Prerequisites 
    - Copy and paste that piece of code into terminal
- Baxter Simulation Installation
- Simulation (After the installation, type these lines of code in terminal)   
    -  ` ./baxter.sh sim` 
    -  `  roslaunch baxter_gazebo baxter_world.launch ` 
-  If the launch is successful then the terminal should be showing these 3 lines of code: 
    -  ` [ INFO] [1400513321.531488283, 34.216000000]: Simulator is loaded and started successfully `

    -  ` [ INFO] [1400513321.535040726, 34.219000000]: Robot is disabled `
    -  ` [ INFO] [1400513321.535125386, 34.220000000]: Gravity compensation was turned off `


Don't worry if the launch is not successful!, It took us many tries in order for the simulator to work successfully. 

There are many errors that we went through and I'll list some of the common ones below, and ways to fix it.

One of the errors could be this: <img width="1444" alt="screen shot 2018-02-12 at 12 36 44 pm" src="https://user-images.githubusercontent.com/31643423/36080212-72cb3b3c-0ff1-11e8-9ea3-ed3f70545aea.png">

To fix this error, you have to change your ip address in the baxter.sh file so in terminal type in : `gedit baxter.sh` and this should pop up: <img width="729" alt="screen shot 2018-02-12 at 12 44 34 pm" src="https://user-images.githubusercontent.com/31643423/36080280-9eed09ba-0ff2-11e8-912b-dbf46a3f23de.png">

Simply change the `your_ip = ...` to your current ip address and the error should go away.

Another way of fixing this error is by going into the Network Adapter settings and changing it to Bridged.

Another common error that we came across can be seen on this screenshot: <img width="927" alt="screen shot 2018-02-12 at 12 52 11 pm" src="https://user-images.githubusercontent.com/31643423/36080364-f6c5e70a-0ff3-11e8-809f-5c758c8c8846.png">

There are 3 things we did to fix this (try all of them if one doesn't work): 

- Reinstall VMWare tools: <img width="1274" alt="screen shot 2018-02-12 at 1 01 22 pm" src="https://user-images.githubusercontent.com/31643423/36080450-255bd98e-0ff5-11e8-8318-95e875b30917.png">

This may be different on Windows but on Mac, go on the Virtual Machine tab and click on Reinstall VMWare Tools, then extract the .gz file and then go to terminal and run `vmware-install.pl`

- Turning off Accelerated 3D Graphics:

Go to the task bar then

<img width="538" alt="screen shot 2018-02-12 at 1 18 47 pm" src="https://user-images.githubusercontent.com/31643423/36080609-5115455e-0ff7-11e8-9327-18d9325a1253.png">

Click on settings and then follow this gif:

![ex](https://user-images.githubusercontent.com/31643423/36080588-0624481a-0ff7-11e8-8bb6-4ddbafc0cf76.gif)

- Install a txt file

We have put in a file in the repository, go to ` /errorFix/baxter_version.txt` and copy that in your Ubunutu and run it via terminal.

Through these 3 fixes, the simulator might work - so first try it.

Use these commands on terminal:

- `cd ros_ws`
- `./baxter.sh sim`
- `roslaunch baxter_gazebo baxter_world.launch`

Now if everything is successful then this should be the output:

~~~ remember to add screenshot ~~~

If this does not work, then keep restarting the simulator by doing this:

- To exit out of previous session ` control + \ `
- `roslaunch baxter_gazebo baxter_world.launch`
- Alternatively restarting the virtual machine might work

After the simulator is running, we have to run the Socket Server:

To do this copy these commands in order in terminal :

- ` cd ros_ws `
- Change the IP Address to your current one : ` gedit socket_server.py `
- To run the server use this command: ` python socket_server.py `

~~~ add screenshot here ~~~ 

## Using Baxter robot with the App

For this part we are going to be doing mostly the same steps as before, so follow these steps from above

- **Setting up Ubuntu**
- **Setting up ROS Workstation and Gazebo Simulator**
- **Setting up the Socket Server**

After you have gone through these steps: go open up a Terminal window in Ubuntu and type in: 

- ` cd ros_ws `
- ` gedit baxter.sh `

and this windows should pop up:  <img width="729" alt="screen shot 2018-02-12 at 12 44 34 pm" src="https://user-images.githubusercontent.com/31643423/36080280-9eed09ba-0ff2-11e8-912b-dbf46a3f23de.png">

From here you will have to change `baxter_hostname=...` to your Baxter robot's hostname which can be found on the back of your robot and `your_ip=...` can be found via terminal or in settings (your ip address).




