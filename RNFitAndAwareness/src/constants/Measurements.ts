class Measurements {
    public static STEPS_COUNT: string = 'stepsCount';
    public static GEOFENCING: string = 'geofence';
    public static AVAILABLE_MEASUREMENTS: object[] = [
        {key: 0, section: true, label: 'Available Measurement'},
        {key: 1, label: 'Steps Count'},
        {key: 2, label: 'Geofencing'}
    ];
}

export default Measurements;
