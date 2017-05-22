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
                title="Stop Measurement"
                color="#F44336"/>
        </View>
      </View>
    );
  }

  requestPermissions() {
      InputKitModule.requestPermissions();
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
  flexItem: {
    margin: 5
  },
  title: {
    fontSize: 20,
    margin: 10,
    alignSelf: 'center',
  }
});
