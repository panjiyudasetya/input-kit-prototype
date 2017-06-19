import React, { Component } from 'react';
import {
  StyleSheet,
  Text,
  View,
  TextInput,
  ListView
} from 'react-native';
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
          style={styles.listViewStyle}
          dataSource={this.state.dataSource}
          renderRow={(rowData) => <Text style={styles.rowItem}>{rowData}</Text>}
          renderSeparator={
            (sectionID: number, rowID: number, adjacentRowHighlighted: boolean) => <View
                key={`${sectionID}-${rowID}`}
                style={{
                  height: adjacentRowHighlighted ? 4 : 1,
                  backgroundColor: adjacentRowHighlighted ? '#3B5998' : '#CCCCCC',
                }}
              />
          }
          enableEmptySections={true}
        />
    );
  }

  fetchData() {
      GoogleFit.reqSharedInstance().then((googleFit) => {
          const startDate = new Date('June 19, 2017 00:00:00');
          const endDate = new Date('June 20, 2017 23:59:00');
          return googleFit.getStepCount(startDate, endDate);
      }).then((data: string) => {
          console.log('JSReceiving steps count data : ' + data);
          const STEPS_COUNT = JSON.parse(data);
          this.notifyDataSourceChange(STEPS_COUNT);
      }).catch((error) => {
          console.log(error);
      });
  }
}

export default StepsCountPage;

const styles = StyleSheet.create({
  listViewStyle: {
    marginBottom: 10,
    flex: 1
  },
  rowItem: {
    fontSize: 14,
    padding: 10,
  },
});
