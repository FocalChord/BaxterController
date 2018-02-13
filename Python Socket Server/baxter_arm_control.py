#!/usr/bin/env python
__author__ = 'Diwakar Somu and Andrew Chen'

'''
    Android app functions added by Dinith Wannigama
    Last Edited: 13/02/2018
'''

#Requires ROS Indigo and Baxter SDK 1.1+

import rospy

import math
import baxter_interface
import tf
#from moveit_commander import conversions
from geometry_msgs.msg import ( PoseStamped,
                                Pose,
                                Point,
                                Quaternion )
from std_msgs.msg import Header, String
from baxter_core_msgs.srv import (SolvePositionIK, SolvePositionIKRequest)

class arm():
    def __init__(self, arm):
        self.arm = arm
        # initialise arm communication channel
        if arm == "right":
            self.pub = rospy.Publisher('arm_rendezvous_right', String, queue_size=5)
            self.sub = rospy.Subscriber('arm_rendezvous_left', String, self.store_sub)
            self.RZsub = rospy.Subscriber('rendezvous_point', String, self.store_RZsub) #Right listens for RZ point
        elif arm == "left":
            self.pub = rospy.Publisher('arm_rendezvous_left', String, queue_size=5)
            self.sub = rospy.Subscriber('arm_rendezvous_right', String, self.store_sub)
            self.RZpub = rospy.Publisher('rendezvous_point', String, queue_size=5) #Left sends the RZ point
        else:
            print "ERROR - initialisation - no valid arm selected"
            return
        print "Rendezvous Channel Initialised"
        self.state = 0 #Initial State
        self.sub_data = 0 #Initial State
        self.RZ = ""
        # initialise ros node
        self.rate = rospy.Rate(5) #5hz when continuously sending

        # gripper ("left" or "right")
        self.gripper = baxter_interface.Gripper(arm)

        # arm ("left" or "right")
        self.limb           = arm
        self.limb_interface = baxter_interface.Limb(self.limb)

    def calibrateGripper(self):
        # gripper ("left" or "right")
        self.gripper = baxter_interface.Gripper(self.limb)
        self.gripper.calibrate()

    #Send a state number to the other node
    def sendSynchro(self, text):
        print "Sending " + str(text)
        self.state = text
        self.pub.publish(str(text))
        
    #Send a state number to the other node, waiting for a response before continuing
    def sendSynchroBlocking(self, text, waitfor):
        print "Sending " + str(text)
        self.pub.publish(str(text))
        while(self.get_sub() < waitfor):
            self.pub.publish(str(text))
            self.sleep()

    #Callback for storing received state number
    def store_sub(self, data):
        try:
            self.sub_data = int(data.data)
            print "Received " + str(self.sub_data)
        except ValueError:
            pass
            
    #Send the RZ position via the appropriate channel
    def send_RZpub(self, text):
        print "Sending " + text
        self.RZpub.publish(text)

    #Callback for storing the RS position from the appropriate channel
    def store_RZsub(self, data):
        print "Received " + data.data
        self.RZ = data.data

    def get_ownstate(self):
        return self.state
        
    def get_sub(self):
        return self.sub_data
        
    def getRZ(self):
        in_str = self.RZ
        input_list = in_str.split(',')
        return [float(i) for i in input_list]
        
    def getRZ_string(self):
        return self.RZ
        
    def sleep(self):
        self.rate.sleep()
        
    def getPose(self):
        #outlist = []
        quaternion_pose = self.limb_interface.endpoint_pose()
        position        = quaternion_pose['position']
        quaternion      = quaternion_pose['orientation']
        euler           = tf.transformations.euler_from_quaternion(quaternion)
        #remember actual position achieved
        self.pose = [position[0], position[1], position[2], euler[0], euler[1], euler[2]]
        #for key, value in pose.iteritems():
        #    temp = (key, value)
        #    outlist.append(temp)
        #return outlist
        return self.pose

    #move a limb
    def baxter_ik_move(self, rpy_pose):   
        #quaternion_pose = conversions.list_to_pose_stamped(rpy_pose, "base")
        quaternion_pose = convert_pose_to_msg(rpy_pose, "base")

        node = "ExternalTools/" + self.limb + "/PositionKinematicsNode/IKService"
        ik_service = rospy.ServiceProxy(node, SolvePositionIK)
        ik_request = SolvePositionIKRequest()
        hdr = Header(stamp=rospy.Time.now(), frame_id="base")

        ik_request.pose_stamp.append(quaternion_pose)
        try:
            rospy.wait_for_service(node, 5.0)
            ik_response = ik_service(ik_request)
        except (rospy.ServiceException, rospy.ROSException), error_message:
            rospy.logerr("Service request failed: %r" % (error_message,))
            print "ERROR - baxter_ik_move - Failed to append pose"
            return 1
            
        if (ik_response.isValid[0]):
            # convert response to joint position control dictionary
            limb_joints = dict(zip(ik_response.joints[0].name, ik_response.joints[0].position))
            
            '''
            NOTE THAT THE METHODS IS FOR THE 'XYZ' CONTROLS ON THE ANDROID APP
            '''
            
            #The method of moving the arms below sets the joint velocities for a set amount of time indicated by
            # the set_command_timeout() time
            #Change the velocity values to find tune the movement and make it smoother if desired (this may also rely on
            # the rate at which commands are sent from the Android app)
            existing_angles = self.limb_interface.joint_angles()
            velocities = {}
            
            vel = 9
           
            if self.arm == "left":
                velocities['left_s0'] = (limb_joints['left_s0'] - existing_angles['left_s0'])*vel
                velocities['left_s1'] = (limb_joints['left_s1'] - existing_angles['left_s1'])*vel
                velocities['left_e0'] = (limb_joints['left_e0'] - existing_angles['left_e0'])*vel
                velocities['left_e1'] = (limb_joints['left_e1'] - existing_angles['left_e1'])*vel
                velocities['left_w0'] = (limb_joints['left_w0'] - existing_angles['left_w0'])*vel
                velocities['left_w1'] = (limb_joints['left_w1'] - existing_angles['left_w1'])*(vel-1)    #The wrists tend to shake, so set 
                velocities['left_w2'] = (limb_joints['left_w2'] - existing_angles['left_w2'])*vel        # the velocity slightly lower
            elif self.arm == "right":
                velocities['right_s0'] = (limb_joints['right_s0'] - existing_angles['right_s0'])*vel
                velocities['right_s1'] = (limb_joints['right_s1'] - existing_angles['right_s1'])*vel
                velocities['right_e0'] = (limb_joints['right_e0'] - existing_angles['right_e0'])*vel
                velocities['right_e1'] = (limb_joints['right_e1'] - existing_angles['right_e1'])*vel
                velocities['right_w0'] = (limb_joints['right_w0'] - existing_angles['right_w0'])*vel
                velocities['right_w1'] = (limb_joints['right_w1'] - existing_angles['right_w1'])*(vel-1)
                velocities['right_w2'] = (limb_joints['right_w2'] - existing_angles['right_w2'])*vel
            
            self.limb_interface.set_command_timeout(0.05) #seconds
            self.limb_interface.set_joint_velocities(velocities)
           
            #USING THE BELOW METHOD (move_to_joint_positons) INSTEAD OF SETTING VElOCITIES IS SLOWER BUT MORE STABLE
            #Un-comment the below command and comment the above commands to use this method instead
            #You must also change the method of sending commands on the android app XyxActivity.java 
            '''
            self.limb_interface.move_to_joint_positions(limb_joints, timeout=0.9)
            '''
        else:
            print "ERROR - baxter_ik_move - No valid joint configuration found"
            return 2

        self.getPose() #Store current pose into self.pose
        print "Move Executed"
        
        return -1


'''
NOTE THAT THE METHOD BELOW IS FOR THE 'JOINTS' CONTROLS ON THE ANDROID APP
'''
#This method simply sets single joint angles       
#Joints: 0 = s0, 1 = s1 (shoulder) / 2 = e0, 3 = e1 (elbow) / 4 = w0, 5 = w1, 6 = w2 (wrist)
def baxter_move_single_joint(limb, joint, value): 
    left1 = baxter_interface.Limb('left')
    right1 = baxter_interface.Limb('right')
    lj1 = left1.joint_names()
    rj1 = right1.joint_names()
    
    if limb == "left_limb":
        current_position1 = left1.joint_angle(lj1[int(joint)])
        joint_command = {lj1[int(joint)]: current_position1 + float(value)}
        left1.set_joint_positions(joint_command)
        return "left done"
            
    elif limb == "right_limb":
        current_position1 = right1.joint_angle(rj1[int(joint)])
        joint_command = {rj1[int(joint)]: current_position1 + float(value)}  
        right1.set_joint_positions(joint_command)
        return "right done"
         
    else:
        return "ERROR: No valid arm selected"
        
        
           

#Because we can't get full moveit_commander on the raspberry pi due to memory limitations, rewritten 
# implementation of the required conversion function      
def convert_pose_to_msg(rpy_pose, target):
    #Modified from conversions.py from move_it
    pose_msg = Pose()
    if len(rpy_pose) == 7:
        [pose_msg.position.x, pose_msg.position.y, pose_msg.position.z] = [rpy_pose[0], rpy_pose[1], rpy_pose[2]]
        [pose_msg.orientation.x, pose_msg.orientation.y, pose_msg.orientation.z, pose_msg.orientation.w] = [rpy_pose[3], rpy_pose[4], rpy_pose[5], rpy_pose[6]]
    elif len(rpy_pose) == 6:
        [pose_msg.position.x, pose_msg.position.y, pose_msg.position.z] = [rpy_pose[0], rpy_pose[1], rpy_pose[2]]
        q = tf.transformations.quaternion_from_euler(rpy_pose[3],rpy_pose[4],rpy_pose[5])
        [pose_msg.orientation.x, pose_msg.orientation.y, pose_msg.orientation.z, pose_msg.orientation.w] = q
    else:
        sys.exit("ERROR - Invalid number of arguments in pose")
    
    pose_msg_stamped = PoseStamped()
    pose_msg_stamped.pose = pose_msg
    pose_msg_stamped.header.frame_id = target
    pose_msg_stamped.header.stamp = rospy.Time.now()
    return pose_msg_stamped
    
    