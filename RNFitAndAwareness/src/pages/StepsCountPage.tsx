import React, { Component } from 'react';
import {
  StyleSheet,
  Text,
  View,
  ListView
} from 'react-native';
import InputKitModule from '../native/InputKitModule';
import Measurements from '../constants/Measurements';
import { DeviceEventEmitter } from 'react-native';

class StepsCountPage extends Component<any, any> {
  constructor(props) {
    super(props);
    this.notifyDataSourceChange();
  }

  componentWillMount() {
    DeviceEventEmitter
        .addListener(
            'InputKitModule',
            (data) => {
                console.log('Emit new event' + data);
                if (data.steps_count_event) {
                    console.log('Steps count' + data.steps_count_event);
                }
            });
  }

  componentDidMount() {
    // this.fetchData();
  }

  notifyDataSourceChange() {
    const ds = new ListView.DataSource({rowHasChanged: (r1, r2) => r1 !== r2});
    this.state = {
      dataSource: ds.cloneWithRows(['Steps count row 1', 'Steps count row 2', 'Steps count row 3']),
    };
  }

  render() {
    return (
      <ListView
        style={styles.containerStyle}
        dataSource={this.state.dataSource}
        renderRow={(data) => <View><Text>{data}</Text></View>}
      />
    );
  }
}

export default StepsCountPage;

const styles = StyleSheet.create({
  containerStyle: {
    flex: 1
  },
  flexItem: {
    margin: 5
  }
});
