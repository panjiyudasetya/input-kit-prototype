import React, { Component } from 'react';
import {
  AppRegistry,
  StyleSheet,
  Text,
  View,
  Alert,
  Button
} from 'react-native';
import { Actions } from 'react-native-router-flux';
import Measurements from '../constants/Measurements';
import {
    GoogleFit,
    Awareness
 } from '../inputkits';

class MainPage extends Component<any, any> {
  componentWillMount() {
      // Do something on mount
  }

  componentDidMount() {
     // Do something on did mount
  }

  render() {
    return (
      <View style={styles.containerStyle}>
        <View style={styles.flexItem}>
            <Text style={styles.title}>
                Input Kit Playground
            </Text>
        </View>
        <View style={styles.flexItem}>
            <Button
                onPress={this.requestPermissions}
                title="Request Permissions"
                color="#03A9F4"/>
        </View>
        <View style={styles.flexItem}>
            <Button
                onPress={this.startMeasurements}
                title="Start Measurements"
                color="#4CAF50"/>
        </View>
        <View style={styles.flexItem}>
            <Button
                onPress={this.stopMeasurements}
                title="Stop Measurements"
                color="#F44336"/>
        </View>
        <View style={styles.flexItem}>
            <Button
                onPress={Actions.goToDashboardPage}
                title="Open Dashboard"
                color="#03A9F4"/>
        </View>
      </View>
    );
  }

  requestPermissions() {
      // InputKitModule.requestPermissions();
      const measurements: [string] = [Measurements.STEPS_COUNT, Measurements.GEOFENCING];
      GoogleFit.reqSharedInstance().then((googleFit) => {
          return googleFit.requestPermissions(measurements);
      }).then(() => {
          console.log('Request Permission done');
      }).catch((error) => {
          console.log(error);
      });
  }

  startMeasurements() {
      GoogleFit.reqSharedInstance().then((googleFit) => {
          return googleFit.startMonitoring('stepsCount');
      }).then(() => {
          console.log('Steps Count measurement started');
      }).catch((error) => {
          console.log(error);
      });

      Awareness.reqSharedInstance().then((awareness) => {
          return awareness.startGeoFencing();
      }).then(() => {
          console.log('Geofencing service started');
      }).catch((error) => {
          console.log(error);
      });
  }

  stopMeasurements() {
      GoogleFit.reqSharedInstance().then((googleFit) => {
          return googleFit.startMonitoring('stepsCount');
      }).then(() => {
          console.log('Steps Count measurement stopped');
      }).catch((error) => {
          console.log(error);
      });

      Awareness.reqSharedInstance().then((awareness) => {
          return awareness.stopGeoFencing();
      }).then(() => {
          console.log('Geofencing service stopped');
      }).catch((error) => {
          console.log(error);
      });
  }
}

export default MainPage;

const styles = StyleSheet.create({
  containerStyle: {
    paddingLeft: 5,
    paddingRight: 5,
    paddingTop: 10,
    flex: 1,
    justifyContent: 'center',
  },
  flexItem: {
    margin: 5
  },
  title: {
    fontSize: 20,
    margin: 10,
    alignSelf: 'center',
  },
  dialogContentView: {
    flex: 1,
    alignItems: 'center',
    justifyContent: 'center',
  },
});
