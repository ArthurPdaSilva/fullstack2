import api from "@/services/api";
import { defineStore } from "pinia";
import { computed, ref } from "vue";
import { useListStore } from "@/features/tasklists/stores/listStore";
import type {
  AuthResponse,
  LoginPayload,
  RegisterPayload,
  User,
} from "../types";

export const useAuthStore = defineStore(
	"auth",
	() => {
		const user = ref<User | null>(null);
		const token = ref<string | null>(null);

		const isAuthenticated = computed(() => !!token.value && !!user.value);

		function parseJwt(jwt: string): Record<string, unknown> {
			try {
				return JSON.parse(atob(jwt.split(".")[1]));
			} catch {
				return {};
			}
		}

		async function login(payload: LoginPayload) {
			const { data } = await api.post<AuthResponse>("/auth/login", payload);
			token.value = data.token;
			const decoded = parseJwt(data.token);
			user.value = {
				id: (decoded.sub as string) || "",
				name: (decoded.name as string) || payload.email.split("@")[0],
				email: payload.email,
			};
			useListStore().load();
		}

		async function register(payload: RegisterPayload) {
			const { data } = await api.post<AuthResponse>("/auth/register", payload);
			token.value = data.token;
			user.value = {
				id: parseJwt(data.token).sub as string,
				name: payload.name,
				email: payload.email,
			};
			useListStore().load();
		}

		function logout() {
			user.value = null;
			token.value = null;
			useListStore().load();
		}

		return { user, token, isAuthenticated, login, register, logout };
	},
	{
		persist: {
			key: "auth",
			storage: localStorage,
			pick: ["user", "token"],
		},
	},
);
