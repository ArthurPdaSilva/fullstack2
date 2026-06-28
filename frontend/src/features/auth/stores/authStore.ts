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

function readRefreshToken(): string | null {
  try {
    const raw = localStorage.getItem("auth_refresh");
    return raw ? JSON.parse(raw) : null;
  } catch {
    return null;
  }
}

function writeRefreshToken(value: string | null) {
  if (value) {
    localStorage.setItem("auth_refresh", JSON.stringify(value));
  } else {
    localStorage.removeItem("auth_refresh");
  }
}

export const useAuthStore = defineStore(
	"auth",
	() => {
		const user = ref<User | null>(null);
		const token = ref<string | null>(null);
		const refreshToken = ref<string | null>(readRefreshToken());

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
			refreshToken.value = data.refreshToken;
			writeRefreshToken(data.refreshToken);
			const decoded = parseJwt(data.token);
			user.value = {
				id: (decoded.sub as string) || "",
				name: (decoded.name as string) || payload.email.split("@")[0],
				email: payload.email,
			};
			useListStore().fetchAll();
		}

		async function register(payload: RegisterPayload) {
			const { data } = await api.post<AuthResponse>("/auth/register", payload);
			token.value = data.token;
			refreshToken.value = data.refreshToken;
			writeRefreshToken(data.refreshToken);
			user.value = {
				id: parseJwt(data.token).sub as string,
				name: payload.name,
				email: payload.email,
			};
			useListStore().fetchAll();
		}

		async function refreshTokenAction() {
			if (!refreshToken.value) throw new Error("No refresh token available");
			const { data } = await api.post<AuthResponse>("/auth/refresh", {
				refreshToken: refreshToken.value,
			});
			token.value = data.token;
			refreshToken.value = data.refreshToken;
			writeRefreshToken(data.refreshToken);
			return data.token;
		}

		function logout() {
			user.value = null;
			token.value = null;
			refreshToken.value = null;
			writeRefreshToken(null);
			useListStore().lists = [];
		}

		return { user, token, refreshToken, isAuthenticated, login, register, refreshTokenAction, logout };
	},
	{
		persist: {
			key: "auth",
			storage: localStorage,
			pick: ["user", "token"],
		},
	} as any,
);
