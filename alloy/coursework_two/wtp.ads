-- Filename: wtp.adb
--
-- Description:         Models the Water Tank Protection System
--

pragma SPARK_Mode (On);
package WTP
with
	Abstract_State => State
is
	procedure Control
	with
	Global => (Output => State),
	Depends => (State => null);
end WTP;
