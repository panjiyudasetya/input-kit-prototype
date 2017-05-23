import React, { Component } from 'react';
import {
  Platform,
  StyleSheet
} from 'react-native';
import { Router, Scene } from 'react-native-router-flux';

import MainPage from './pages/MainPage';
import DashboardPage from './pages/DashboardPage';

class RNFitAndAwareness extends Component<any, any> {
  render() {
    return (
      <Router
        hideNavBar={false}
        navigationBarStyle={styles.navigation}>
        <Scene key="root">
          <Scene key="goToMainPage" component={MainPage} title="" initial={true} />
          <Scene key="goToDashboardPage" component={DashboardPage} title="Input Kit Dashboard" />
        </Scene>
      </Router>
    );
  }
}

export default RNFitAndAwareness;

const styles = StyleSheet.create({
  navigation: {
    backgroundColor: '#fff',
    borderBottomColor: 'transparent',
    borderBottomWidth: (Platform.OS !== 'ios' ? 54 : 64)
  }
});
