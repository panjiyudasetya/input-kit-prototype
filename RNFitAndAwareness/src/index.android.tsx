import React, { Component } from 'react';
import {
  AppRegistry,
  StyleSheet,
  Text,
  View,
  Alert,
  Button
} from 'react-native';
import InputKitModule from './native/InputKitModule';
import Measurements from './constants/Measurements';
import { DeviceEventEmitter } from 'react-native';

class RNFitAndAwareness extends Component<any, any> {
  componentWillMount() {
    DeviceEventEmitter
        .addListener(
            'InputKitModule',
            (data) => {
                console.log(data.content);
            });
  }

  componentDidMount() {
    // this.fetchData();
  }

  render() {
    return (
      <View style={styles.containerStyle}>

        <Text style={styles.title}>
          Input Kit Prototype
        </Text>

        <View style={styles.boxStyle}>

          <Button
            onPress={this.requestPermissions}
            title="Request Permissions"
            color="#841584"
          />

          <Button
            onPress={this.startMeasurements}
            title="Start Measurement"
            color="#841584"
          />

          <Button
            onPress={this.stopMeasurements}
            title="Sending emit events"
            color="#841584"
          />

        </View>
      </View>
    );
  }

  requestPermissions() {
      const measurements = [Measurements.STEPS_COUNT, Measurements.GEOFENCING];
      InputKitModule.requestPermissions(measurements);
  }

  startMeasurements() {
      const measurements = [Measurements.STEPS_COUNT, Measurements.GEOFENCING];
      InputKitModule.startMeasurements(measurements);
  }

  stopMeasurements() {
      const measurements = [Measurements.STEPS_COUNT, Measurements.GEOFENCING];
      InputKitModule.stopMeasurements(measurements);
  }

  async fetchData() {
      const response = await fetch('http://calapi.inadiutorium.cz/api/v0/en/calendars/default/today');
      const json = await response.json();
      console.log(json);
  }
}

export default RNFitAndAwareness;

const styles = StyleSheet.create({
  containerStyle: {
    borderWidth: 1,
    borderRadius: 2,
    borderColor: '#ddd',
    borderBottomWidth: 0,
    shadowColor: '#000',
    shadowOffset: { width: 0, height: 2 },
    shadowOpacity: 0.1,
    shadowRadius: 2,
    elevation: 1,
    marginLeft: 5,
    marginRight: 5,
    marginTop: 10,
    flex: 1,
    justifyContent: 'center',
  },
  boxStyle: {
    flexDirection: 'column',
    justifyContent: 'space-between',
    marginLeft: 15,
    marginRight: 15
  },
  title: {
    fontSize: 20,
    margin: 10,
    alignSelf: 'center',
  },
  buttonStyle: {
    backgroundColor: '#fff',
    borderRadius: 5,
    borderWidth: 1,
    borderColor: '#007aff',
  }
});
