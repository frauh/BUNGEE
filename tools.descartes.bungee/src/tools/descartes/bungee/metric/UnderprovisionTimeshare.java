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

package tools.descartes.bungee.metric;

import java.util.List;

import tools.descartes.bungee.allocation.ResourceAllocation;
import tools.descartes.bungee.utils.FileUtility;

public class UnderprovisionTimeshare extends AbstractAreaDuration {
	static final String NAME = "timeshare_U";
	
	String explanation = null;
	String csvString = null;
	
	
	public UnderprovisionTimeshare(List<ResourceAllocation> demand,
			List<ResourceAllocation> supply) {
		super(demand, supply);
	}

	@Override
	protected double evaluate() {
		Provisioning provision = getProvisioning();
		long duration = getDuration();
		double result = provision.timeUnder / duration;
		gernerateExplanantion(provision.timeUnder, result);
		generateCSV(provision.timeUnder, result);
		return result;
	}
	
	private void generateCSV(double underprovisionTime, double result) {
		csvString = "underprovision_timeshare" + FileUtility.CSV_SPLIT_BY + result + System.lineSeparator();
		csvString += "duration [min]" + FileUtility.CSV_SPLIT_BY + getDuration() / MINUTE_TO_MILLIS + System.lineSeparator();
		csvString += "underprovision_time [min]" + FileUtility.CSV_SPLIT_BY + underprovisionTime / MINUTE_TO_MILLIS + System.lineSeparator();
		
	}

	private void gernerateExplanantion(double underprovisionTime, double result) {
		explanation = "underprovision_timeshare: " + result + System.lineSeparator();
		explanation += "duration [min]: " + getDuration() / MINUTE_TO_MILLIS + System.lineSeparator();
		explanation += "underprovision_time [min]: " + underprovisionTime / MINUTE_TO_MILLIS + System.lineSeparator();
	}

	@Override
	protected String textShort() {
		if (explanation == null) {
			evaluate();
		}
		return explanation;
	}

	@Override
	protected String csvString() {
		if (csvString == null) {
			evaluate();
		}
		return csvString;
	}

	@Override
	public String getName() {
		return "timeshare_U";
	}

}
