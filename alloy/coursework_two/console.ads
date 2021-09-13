
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
-- Filename:            console.ads
--
-- Description:         Models the console associated with the WTP system, i.e. 
--                      the reset mechanism that is required to close the 
--                      emergency drainage valve.


pragma SPARK_Mode (On); 
package  Console
	with 
	Abstract_State => State
is
   
   procedure Enable_Reset
   with
   Global => (Output => State),
   Depends => (State => null);

   procedure Disable_Reset
   with
   Global => (Output => State),
   Depends => (State => null);

   function Reset_Enabled return Boolean
   with
   Global => (Input => State),
   Depends => (Reset_Enabled'Result => State);  
   
  
   
end Console;



