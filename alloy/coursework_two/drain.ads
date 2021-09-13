
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
-- Filename:            drain.ads
--
-- Description:         Models the emergency drainage valve associated with the 
--                      water tank.

pragma SPARK_Mode (On);
package Drain
with
	Abstract_State => State
is
   procedure Open
   with
   Global => (Output => State),
   Depends => (State => null);

   procedure Close
   with
   Global => (Output => State),
   Depends => (State => null);

   function Openned return Boolean
   with
   Global => (Input => State),
   Depends => (Openned'Result => State);

end Drain;



