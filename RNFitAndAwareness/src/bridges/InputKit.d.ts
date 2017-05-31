import Region from '../inputkits/Location';

declare interface GoogleFitBridge {
    isGoogleApiClientConnected(): Promise<any>;
    requestPermissions(types: [string]): Promise<any>;
    getStepCount(date: number): Promise<any>;
    startMonitoring(type: string): Promise<any>;
    stopMonitoring(type: string): Promise<any>;
}

declare interface AwarenessBridge {
    requestPermissions(): Promise<any>;
    startGeoFencing(): Promise<any>;
    stopGeoFencing(): Promise<any>;
    getGeoFencingHistory():Promise<any>;
}