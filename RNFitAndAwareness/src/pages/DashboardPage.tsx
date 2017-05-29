import React, { Component } from 'react';
import {
  Platform,
  StyleSheet,
  View
} from 'react-native';
import StepsCountPage from './StepsCountPage';
import GeofencingPage from './GeofencingPage';
import ScrollableTabView from 'react-native-scrollable-tab-view';

class DashboardPage extends Component<any, any> {
  componentDidMount() {
    // this.fetchData();
  }

  render() {
    return (
      <ScrollableTabView
         style={styles.tabView}
         tabBarActiveTextColor={'#03A9F4'}
         tabBarUnderlineStyle={{ backgroundColor: '#03A9F4' }}>
            <StepsCountPage tabLabel="Steps Count" />
            <GeofencingPage tabLabel="Geofencing" />
      </ScrollableTabView>
    );
  }
}

export default DashboardPage;

const styles = StyleSheet.create({
  tabView: {
    marginTop: (Platform.OS !== 'ios' ? 54 : 64)
  }
});
