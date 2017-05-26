import React, { Component } from 'react';
import {
  StyleSheet,
  Text,
  View,
  ListView
} from 'react-native';
import InputKitModule from '../native/InputKitModule';
import Measurements from '../constants/Measurements';
import Row from '../components/itemrows/ContentItemRow';
import { DeviceEventEmitter } from 'react-native';

class GeofencingPage extends Component<any, any> {
  constructor(props) {
    super(props);
    this.notifyDataSourceChange([]);
  }

  componentWillMount() {
    DeviceEventEmitter
        .addListener(
            'GeofencingEmitEvent',
            (data: string) => {
                console.log('JSGeofence > Emit new event' + data);
                const GEOFENCE = JSON.parse(data);
                if (GEOFENCE.geofence_event) {
                    this.notifyDataSourceChange(GEOFENCE.geofence_event);
                    this.forceUpdate();
                    console.log('JSGeofence > Geofence > ' + GEOFENCE.geofence_event);
                }
            });
  }

  componentDidMount() {
    this.fetchData();
  }

  notifyDataSourceChange(contents: object[]) {
    const ds = new ListView.DataSource({rowHasChanged: (r1, r2) => r1 !== r2});
    this.state = {
      dataSource: ds.cloneWithRows(contents),
    };
  }

  render() {
    return (
      <ListView
        style={styles.containerStyle}
        dataSource={this.state.dataSource}
        renderRow={(data) => <Row {...data}
        enableEmptySections={true}/>}
      />
    );
  }

  fetchData() {
      InputKitModule.getGeofencingHistory();
  }
}

export default GeofencingPage;

const styles = StyleSheet.create({
  containerStyle: {
    flex: 1
  },
  flexItem: {
    margin: 5
  }
});
