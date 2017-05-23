import React, { Component } from 'react';
import {
  AppRegistry,
  StyleSheet,
  Text,
  View,
  Alert,
  Button
} from 'react-native';
import InputKitModule from '../native/InputKitModule';
import Measurements from '../constants/Measurements';
import { DeviceEventEmitter } from 'react-native';

class StepsCountPage extends Component<any, any> {
  static navigationOptions = {
    tabBarLabel: 'Steps Count'
  };

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
                Steps Count Page
            </Text>
        </View>
      </View>
    );
  }
}

export default StepsCountPage;

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
