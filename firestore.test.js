const { initializeTestEnvironment, assertFails, assertSucceeds } = require("@firebase/rules-unit-testing");
const { test, before, after, beforeEach } = require("node:test");
const fs = require("node:fs");

const PROJECT_ID = "demo-no-project";
let testEnv;

before(async () => {
  testEnv = await initializeTestEnvironment({
    projectId: PROJECT_ID,
    firestore: {
      rules: fs.readFileSync("firestore.rules", "utf8"),
      host: "127.0.0.1",
      port: 8085,
    },
  });
});

after(async () => {
  if (testEnv) {
    await testEnv.cleanup();
  }
});

beforeEach(async () => {
  if (testEnv) {
    await testEnv.clearFirestore();
  }
});

test("unauthenticated user cannot read or write user profile", async () => {
  const unauthDb = testEnv.unauthenticatedContext().firestore();
  await assertFails(unauthDb.collection("users").doc("user_123").get());
  await assertFails(
    unauthDb.collection("users").doc("user_123").set({
      email: "test@example.com",
      displayName: "Test User",
      createdAt: new Date(),
      updatedAt: new Date(),
    })
  );
});

test("authenticated user can create own profile and read it", async () => {
  const aliceDb = testEnv.authenticatedContext("alice_123").firestore();
  await assertSucceeds(
    aliceDb.collection("users").doc("alice_123").set({
      email: "alice@example.com",
      displayName: "Alice",
      miles: 1000,
      preferredTier: "BALANCED",
      createdAt: new Date(),
      updatedAt: new Date(),
    })
  );

  const doc = await assertSucceeds(aliceDb.collection("users").doc("alice_123").get());
  // Verify cross-user isolation: bob cannot read alice's document
  const bobDb = testEnv.authenticatedContext("bob_456").firestore();
  await assertFails(bobDb.collection("users").doc("alice_123").get());
});

test("authenticated user can create booking and cross-user cannot read", async () => {
  const aliceDb = testEnv.authenticatedContext("alice_123").firestore();
  await assertSucceeds(
    aliceDb.collection("users").doc("alice_123").collection("bookings").doc("b_1").set({
      pnr: "TP-123456",
      title: "HYB VSG Express 3A",
      type: "Train",
      routeOrLocation: "HYB -> MAO",
      travelDate: "15 Nov 2026",
      totalAmountInr: 2760,
      status: "CONFIRMED",
      seatOrRoomNumber: "B2-24",
      createdAt: new Date(),
      updatedAt: new Date(),
    })
  );

  const bobDb = testEnv.authenticatedContext("bob_456").firestore();
  await assertFails(bobDb.collection("users").doc("alice_123").collection("bookings").doc("b_1").get());
});
