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
import InputKit from '../inputkits';
import { DeviceEventEmitter } from 'react-native';
import {
    GoogleFit,
    GoogleFitDelegate
 } from '../inputkits';

class StepsCountPage extends Component<any, any> implements GoogleFitDelegate {
  constructor(props) {
    super(props);
    const ds = new ListView.DataSource({rowHasChanged: (r1, r2) => r1 !== r2});
    this.state = {
      dataSource: ds.cloneWithRows([]),
    };
  }

  onGoogleFitUpdates(eventName: string, success: boolean): void {
    console.debug('Receiving google fit updates.');
  }

  componentWillMount() {
    GoogleFit.reqSharedInstance().then((googleFit) => {
        googleFit.setDelegate(this);
    });
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
        enableEmptySections={true} />}
      />
    );
  }

  fetchData() {
      GoogleFit.reqSharedInstance().then((googleFit) => {
          return googleFit.getStepCount(new Date());
      }).then((data: string) => {
          const STEPS_COUNT = JSON.parse(data);
          this.notifyDataSourceChange(STEPS_COUNT);
      }).catch((error) => {
          console.log(error);
      });
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
