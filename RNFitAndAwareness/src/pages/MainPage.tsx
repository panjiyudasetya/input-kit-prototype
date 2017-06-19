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
import { GoogleFit } from '../inputkits';

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
                onPress={Actions.goToDashboardPage}
                title="Open Dashboard"
                color="#03A9F4"/>
        </View>
      </View>
    );
  }

  requestPermissions() {
      // InputKitModule.requestPermissions();
      const measurements: [string] = [Measurements.STEPS_COUNT];
      GoogleFit.reqSharedInstance().then((googleFit) => {
          return googleFit.requestPermissions(measurements);
      }).then(() => {
          console.log('Request Permission done');
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
