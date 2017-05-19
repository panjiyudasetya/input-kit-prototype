import { NativeEventEmitter, NativeModules } from 'react-native';
// const { InputKitModule } = NativeModules;

// const testingModuleEmitter = new NativeEventEmitter(InputKitModule);

// const subscription = testingModuleEmitter.addListener(
//   'InputKitModule', (data) => { console.log(data.content); }, null);

// export default InputKitModule;

export default NativeModules.InputKitModule;
