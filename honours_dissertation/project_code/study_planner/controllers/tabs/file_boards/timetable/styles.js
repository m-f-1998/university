import { StyleSheet, Dimensions } from 'react-native';

/*
  ==========================================
   Title: Styles For Timetable
   Description: Common Styles Between Timetable Component
  ==========================================
*/

export default StyleSheet.create( {
  container: {
    flex: 1,
    backgroundColor: '#0B345A',
    height: Dimensions.get( 'window' ).height,
    width: '100%',
    justifyContent: 'center',
    alignContent: 'center'
  },
  rowButton: {
    textAlign: 'center',
    color: 'white',
    fontWeight: 'bold'
  },
  openTouch: {
    alignItems: "center",
    justifyContent: "center",
    alignSelf: 'center',
    paddingVertical: 5,
    borderRadius: 4,
    marginTop: 10,
    paddingLeft: 5,
    paddingRight: 5,
    marginLeft: 5,
    backgroundColor: '#6b41de'
  }
} );
