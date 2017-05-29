class Measurements {
    public static STEPS_COUNT: number = 0;
    public static GEOFENCING: number = 1;
    public static AVAILABLE_MEASUREMENTS: object[] = [
        {key: 0, section: true, label: 'Steps Count'},
        {key: 1, label: 'Steps Count'},
        {key: 2, label: 'Geofencing'}
    ];
}

export default Measurements;
