import React, { Component } from 'react';
import {
  StyleSheet,
  Text,
  View,
  ListView
} from 'react-native';
import InputKitModule from '../native/InputKitModule';
import Measurements from '../constants/Measurements';
import EmitterEventListener from '../constants/EmitterEventListener';
import Row from '../components/itemrows/ContentItemRow';
import { DeviceEventEmitter } from 'react-native';
let IS_COMPONENT_MOUNTED: boolean = false;

class GeofencingPage extends Component<any, any> {
  constructor(props) {
    super(props);
    props.isMounted = false;
    this.notifyDataSourceChange([]);
  }

  componentWillMount() {
    DeviceEventEmitter
        .addListener(
            EmitterEventListener.GEOFENCING_EVENT_LISTENER,
            (data: string) => {
                console.log('JSGeofence > Emit new event' + data);
                const GEOFENCE = JSON.parse(data);
                if (GEOFENCE.geofence_event && IS_COMPONENT_MOUNTED === true) {
                    this.notifyDataSourceChange(GEOFENCE.geofence_event);
                    this.forceUpdate();
                    console.log('JSGeofence > Geofence > ' + GEOFENCE.geofence_event);
                }
            });
  }

  componentWillUnmount() {
      IS_COMPONENT_MOUNTED = false;
      DeviceEventEmitter.removeListener(
          EmitterEventListener.GEOFENCING_EVENT_LISTENER,
          () => {
            console.log(EmitterEventListener.GEOFENCING_EVENT_LISTENER + ' removed.');
          }
      );
  }

  componentDidMount() {
    IS_COMPONENT_MOUNTED = true;
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
