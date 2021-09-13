/**  
* WTP.als - a simple model of a Water Tank Protection system
* @see http://alloytools.org
*/ 

--------------- Signatures ---------------

one abstract sig WaterLevel {}
sig Low, Normal, High extends WaterLevel {}

one sig higherLevelWTPControls {
	controllerControl: ControllerSubSys,
	alarmControl: AlarmSubSys,
	sensorControl: set SensorsSubSys,
	consoleControl: ConsoleSubSys,
	drainControl: DrainSubSys
}

lone sig ControllerSubSys {
	activateAlarm: lone AlarmSubSys,
	drainOpen: lone DrainSubSys
}

lone sig AlarmSubSys, DrainSubSys {}
sig SensorsSubSys {
	reading: lone WaterLevel,
	waterValue: lone ControllerSubSys
}
lone sig ConsoleSubSys {
	resetCmd: lone ControllerSubSys,
	alarmCount: lone AlarmSubSys
}

--------------- Facts ---------------

fact {

	-- Three Sensors Cardinality Constraint --
	all n: higherLevelWTPControls  | #(n.sensorControl) = 3

	-- Sensors Should Return Same Reading --
	all s1, s2: SensorsSubSys | s1.reading = s2.reading
	
	-- If Drain Open Ignores System Input --
	all c: ControllerSubSys, w: higherLevelWTPControls, s: SensorsSubSys | c.drainOpen = w.drainControl => #(s.waterValue) = 0 else #(s.waterValue) = 1

	-- If Alarm Active Make A Count On Console --
	all c: ControllerSubSys, w:higherLevelWTPControls | c.activateAlarm = w.alarmControl => w.consoleControl.alarmCount = w.alarmControl else no w.consoleControl.alarmCount

}



--------------- Predicates --------------- 

/**

	- Normal Is In Sensor Readings
	- Alarm Sould Be Stopped
	- Drain Should Be Closed
	- No Need To Reset Tank

**/

pred normalWaterLevel[ r: Normal, s: SensorsSubSys, cons: ConsoleSubSys, c: ControllerSubSys, w: higherLevelWTPControls ] {
	r in s.reading
	no c.activateAlarm
	no c.drainOpen
	no cons.resetCmd
}

/**

	- High Is In Sensor Readings
	- Alarm Activated
	- Drain Either Open If Alarm Still Activated Or Not ( Marked as 'lone' For Open/Closed )
	- No Need To Reset Tank

**/

pred highWaterLevel[ r: High, s: SensorsSubSys, cons: ConsoleSubSys, c: ControllerSubSys, w: higherLevelWTPControls ] {
	r in s.reading
	c.activateAlarm = w.alarmControl
	no cons.resetCmd
}

/**

	- Low Is In Sensor Readings
	- Alarm Activated
	- Drain Should be Closed
	- No Need To Reset Tank

**/

pred lowWaterLevel[ r: Low, s: SensorsSubSys, cons: ConsoleSubSys, c: ControllerSubSys, w: higherLevelWTPControls ] {
	r in s.reading
	c.activateAlarm = w.alarmControl
	no cons.resetCmd
	no c.drainOpen
}


/**

	- Low Or Normal Is In Sensor Readings
	- Alarm Should Be Stopped
	- Drain Should be Closed
	- Console Reset Mechanism Called

**/

pred resetWaterLevel[ r: WaterLevel, s: SensorsSubSys, cons: ConsoleSubSys, c: ControllerSubSys, w: higherLevelWTPControls ] {
	all r1: High | r != r1
 	r in s.reading
	no c.activateAlarm
	no c.drainOpen
	cons.resetCmd = c
}

--------------- Run ---------------

 
run normalWaterLevel for 3
run highWaterLevel for 3
run lowWaterLevel for 3
run resetWaterLevel for 3
