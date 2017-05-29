import React, { Component } from 'react';
import { Router, Scene } from 'react-native-router-flux';

import MainPage from './pages/MainPage';
import DashboardPage from './pages/DashboardPage';

class RNFitAndAwareness extends Component<any, any> {
  render() {
    return (
      <Router>
        <Scene key="root">
          <Scene key="goToMainPage" component={MainPage} title="Input Kit Playground" initial={true} />
          <Scene key="goToDashboardPage" component={DashboardPage} title="Input Kit Dashboard" />
        </Scene>
      </Router>
    );
  }
}

export default RNFitAndAwareness;
