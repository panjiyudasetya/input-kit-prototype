// tslint:disable-next-line:no-reference
/// <reference path="../bridges/InputKit.d.ts" />

import { NativeModules, NativeEventEmitter } from 'react-native';
import EmitterEventListener from '../constants/EmitterEventListener';

const GoogleFitBridge = NativeModules.GoogleFitBridge;
const googleFitBridgeEmitter = new NativeEventEmitter(GoogleFitBridge);

class GoogleFit {

    private googleFitBridge: GoogleFitBridge;
    private delegate: GoogleFitDelegate;

    constructor() {
        this.googleFitBridge = GoogleFitBridge;
    }

    setDelegate(delegate: GoogleFitDelegate) {
        this.delegate = delegate;
    }

    isHealthAvailable() {
        return this.googleFitBridge.isHealthAvailable();
    }

    requestPermissions(types: [string]) {
        console.log(types);
        return this.googleFitBridge.requestPermissions(types);
    }

    getStepCount(startDate: Date, endDate: Date) {
        return this.googleFitBridge.getStepCount(startDate.getTime(), endDate.getTime());
    }

    getStepCountDistribution(startDate: Date, endDate: Date, interval: string) {
        return this.googleFitBridge.getStepCountDistribution(startDate.getTime(), endDate.getTime(), interval);
    }
}

export interface GoogleFitDelegate {
    onGoogleFitUpdates(eventName: string, success: boolean): void;
}

export type GoogleFitType = 'stepsCount';
let googleFit: GoogleFit = null;

export default {
    reqSharedInstance: (): Promise<GoogleFit> => {
        if (googleFit == null) {
            googleFit = new GoogleFit();
            return Promise.resolve(googleFit);
        }
        return Promise.resolve(googleFit);
    }

};
