import React, { Component } from 'react';
import {
  StyleSheet,
  Text,
  View,
  ListView
} from 'react-native';
import Measurements from '../constants/Measurements';
import EmitterEventListener from '../constants/EmitterEventListener';
import Row from '../components/itemrows/ContentItemRow';
import {
    Awareness,
    AwarenessDelegate
 } from '../inputkits';

class GeofencingPage extends Component<any, any> implements AwarenessDelegate {
    constructor(props) {
    super(props);
    const ds = new ListView.DataSource({rowHasChanged: (r1, r2) => r1 !== r2});
    this.state = {
      dataSource: ds.cloneWithRows([]),
    };
  }

  onAwarenessUpdated(eventName: string, success: boolean): void {
    console.log('Receiving awareness updates.');
  }

  componentDidMount() {
    this.fetchData();
  }

  notifyDataSourceChange(contents: object[]) {
    this.setState({
      dataSource: this.state.dataSource.cloneWithRows(contents),
    });
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
      Awareness.reqSharedInstance().then((awareness) => {
          return awareness.getGeoFencingHistory();
      }).then((data: string) => {
          console.log('JSReceiving geofencing data : ' + data);
          const GEOFENCE_HISTORY = JSON.parse(data);
          this.notifyDataSourceChange(GEOFENCE_HISTORY);
      }).catch((error) => {
          console.log(error);
      });
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
