import React from 'react';
import {
    View,
    Text,
    StyleSheet,
    Image
} from 'react-native';

// tslint:disable-next-line:variable-name
const Row = (props) => (
  <View style={styles.container}>
    <Text style={styles.textContent}>
      {`${props.content}`}
    </Text>
    <Text style={styles.textTimeStamp}>
      {`Your first event recorded at : \n${new Date(props.time_stamp).toLocaleString()}`}
    </Text>
  </View>
);

export default Row;

const styles = StyleSheet.create({
  container: {
    flex: 1,
    padding: 12,
  },
  textContent: {
    margin: 5,
    fontSize: 14,
  },
  textTimeStamp: {
    marginLeft: 5,
    fontSize: 12,
    fontStyle: 'italic',
    fontWeight: 'bold',
  }
});
