
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
-- Filename:            handler.ads
--
-- Description:         Provides the drivers required for simulating the
--                      environment in which the WTP system operates as
--                      well as a logging capability. 

pragma SPARK_Mode (Off);
package Handler is

  subtype Sensor_Range is Integer range 0..2100;
   
  type Water_Level_Cat is (Low, Normal, High, Undef);
   
  procedure Update_Env;

  function At_End return Boolean;

  procedure Open_Env_File;

  procedure Close_Env_File;

  procedure Update_Log;

  procedure Open_Log_File;

  procedure Close_Log_File;

end Handler;



