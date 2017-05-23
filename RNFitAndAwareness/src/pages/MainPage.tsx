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
import InputKitModule from '../native/InputKitModule';
import Measurements from '../constants/Measurements';
import { DeviceEventEmitter } from 'react-native';

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
}

export default MainPage;

const styles = StyleSheet.create({
  containerStyle: {
    shadowColor: '#000',
    shadowOffset: { width: 0, height: 2 },
    shadowOpacity: 0.1,
    shadowRadius: 2,
    elevation: 1,
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
  }
});
