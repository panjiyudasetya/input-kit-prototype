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

class StepsCountPage extends Component<any, any> {
  constructor(props) {
    super(props);
    const ds = new ListView.DataSource({rowHasChanged: (r1, r2) => r1 !== r2});
    this.state = {
      dataSource: ds.cloneWithRows([]),
    };
  }

  componentWillMount() {
    DeviceEventEmitter
        .addListener(
            EmitterEventListener.STEPS_COUNT_EVENT_LISTENER,
            (data: string) => {
                console.log('JSStepsCount > Emit new event' + data);
                const STEPS_COUNT = JSON.parse(data);
                if (STEPS_COUNT.steps_count_event) {
                    this.notifyDataSourceChange(STEPS_COUNT.steps_count_event);
                    console.log('JSStepsCount > Steps count > ' + STEPS_COUNT.steps_count_event);
                }
            });
  }

  componentWillUnmount() {
      DeviceEventEmitter.removeListener(
          EmitterEventListener.STEPS_COUNT_EVENT_LISTENER,
          () => {
            console.log(EmitterEventListener.STEPS_COUNT_EVENT_LISTENER + ' removed.');
          }
      );
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
      InputKitModule.getStepsCountHistory();
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
