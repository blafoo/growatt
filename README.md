# growatt

API to query data from server.growatt.com for Growatt inverters.

## Usage

1. Create a `GrowattWebClient` instance
1. Login
1. Query data

```
GrowattWebClient client = new GrowattWebClient();
client.login(new LoginRequest("account", "password"));
DayResponse day = client.getInvEnergyDayChart(new EnergyRequest(client.getPlantId(), "2023-05-31"));
```

## Examples

1. Unit test `GrowattWebClientTest`
1. [Vaadin 24 project using the `GrowattWebClient`](https://github.com/blafoo/BKW)