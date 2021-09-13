-- Author:              A. Ireland
--
-- Address:             School Mathematical & Computer Sciences
--                      Heriot-Watt University
--                      Edinburgh, EH14 4AS
--
-- E-mail:              a.ireland@hw.ac.uk
--
-- Last modified:       13.9.2019
--
-- Filename:            alarm.ads
--
-- Description:         Models the alarm device associated with
--                      the WTP controller and the alarm count.

pragma SPARK_Mode (On);
package Alarm
	with
	Abstract_State => State
is
   procedure Enable
   with
   Global  => (In_Out => State),
   Depends => (State  => State);

   procedure Disable
   with
   Global  => (In_Out => State),
   Depends => (State  => State);

   function Enabled return Boolean
   with
   Global => (Input => State),
   Depends => (Enabled'Result => State);

   function Alarm_Cnt_Value return Integer
   with
   Global => (Input => State),
   Depends => (Alarm_Cnt_Value'Result => State);

end Alarm;
