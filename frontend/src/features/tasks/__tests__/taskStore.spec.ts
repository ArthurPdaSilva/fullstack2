import { createPinia, setActivePinia } from "pinia";
import { beforeEach, describe, expect, it } from "vitest";
import { useTaskStore } from "../stores/taskStore";

describe("taskStore", () => {
	beforeEach(() => {
		setActivePinia(createPinia());
	});

	it("starts with empty tasks", () => {
		const store = useTaskStore();
		expect(store.tasks).toHaveLength(0);
		expect(store.pendingTasks).toHaveLength(0);
		expect(store.completedTasks).toHaveLength(0);
	});
});
