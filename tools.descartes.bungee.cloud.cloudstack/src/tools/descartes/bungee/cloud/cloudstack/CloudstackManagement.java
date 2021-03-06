/*******************************************************************************
Copyright 2015 Andreas Weber, Nikolas Herbst

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*******************************************************************************/

package tools.descartes.bungee.cloud.cloudstack;

import java.io.File;

import tools.descartes.bungee.cloud.Bounds;
import tools.descartes.bungee.cloud.CloudManagement;
import tools.descartes.bungee.cloud.ResourceWatch;

public class CloudstackManagement implements CloudManagement {
	private CloudstackControllerImpl cloud;
	//TODO: remove netscaler stuff completely?
	//private NetscalerController netscaler;
	private CloudSettings cloudSettings;
	private ResourceWatch resWatcher;

	public CloudstackManagement(CloudstackControllerImpl cloudStackImpl) {
		cloud = cloudStackImpl;
		//TODO: remove netscaler stuff completely?
		//netscaler = NetscalerController.getInstance();
		resWatcher = new ResourceWatch(this);
	}
	
	public CloudstackManagement(File cloudstackPropertiesFile) {
		this(new CloudstackControllerImpl(cloudstackPropertiesFile));
	}

	public boolean createStaticCloud(String hostName, int resourceAmount) {
		boolean success = false;
		// delete an old cloud which could still exist
		cleanUpCloudAtIp(hostName);

		// adapt cloud settings
		cloudSettings.setIp(hostName);
		cloudSettings.setMinInstances(resourceAmount);
		cloudSettings.setMaxInstances(resourceAmount);

		// create cloud
		String createAutoScaleVMGroupId = cloud.createAutoScaleVMGroup(cloudSettings);
		boolean createdSuccess = (createAutoScaleVMGroupId != null) && !createAutoScaleVMGroupId.isEmpty();
		if (createdSuccess) {
			boolean cloudAtCorrectAmount = resWatcher.waitForResourceAmount(hostName, new Bounds(resourceAmount,resourceAmount));
			if (cloudAtCorrectAmount) {
				success = true;
				//disable autoscaling because we want a static cloud
				cloud.disableAutoScaleGroup(hostName);
				//TODO: remove netscaler stuff completely?
				//remap the public ip within the load balancer from the private network to the plublic network
				//This is just because of the strange network setup at FZI uncomment to use in FZI setup
				//netscaler.remapLbIp(hostName, IPMap.getInstance().getPublicIP(hostName));
			} else {
				cloud.deleteAutoScaleVMGroupByIp(hostName);
			}
		}
		return success;
	}

	public boolean destroyCloud(String hostName) {
		return cloud.deleteAutoScaleVMGroupByIp(hostName);
	}

	public CloudSettings getCloudSettings() {
		return cloudSettings;
	}

	public void setCloudSettings(CloudSettings cloudSettings) {
		this.cloudSettings = cloudSettings;
	}

	public void setCloudSettings(File cloudSettings) {
		this.cloudSettings = CloudSettings.load(cloudSettings);
	}

	private void cleanUpCloudAtIp(String hostName) {
		String groupId = cloud.getAutoScaleGroupId(hostName); 
		if (groupId != null)
		{
			boolean success = cloud.deleteAutoScaleVMGroupByIp(hostName);
			System.out.println("Cleanup: Delete VM group " + groupId + " successful: " + success);
		}
		String lbId = cloud.getLBId(hostName); 
		if (lbId != null)
		{
			boolean success = cloud.deleteLoadBalancerRuleByIp(hostName);
			System.out.println("Cleanup: Delete LB Rule " + lbId + " successful: " + success);
		}
	}
	
	@Override
	public Bounds getScalingBounds(String hostName) {
		return cloud.getScalingBounds(hostName);
	}

	@Override
	public boolean setScalingBounds(String hostName, Bounds bounds) {
		cloud.disableAutoScaleGroup(hostName);
		boolean success = cloud.updateAutoScaleVMGroup(hostName, bounds.getMin(), bounds.getMax());
		cloud.enableAutoScaleGroup(hostName);
		//TODO: remove netscaler stuff completely?
		//remap the public ip within the load balancer from the private network to the plublic network
		//This was just because of the strange network setup at FZI, uncomment
		//netscaler.remapLbIp(hostName, IPMap.getInstance().getPublicIP(hostName));
		return success;
	}

	@Override
	public int getNumberOfResources(String ip) {
		return cloud.getNumberOfResources(ip);
	}

	
}
