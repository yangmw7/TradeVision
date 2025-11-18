import client from './client';

export interface SubscriptionPlan {
  id: number;
  name: string;
  nameKo: string;
  description: string;
  price: number;
  billingPeriod: string;
  features: string[];
  maxAnalysesPerMonth: number;
  isActive: boolean;
}

export interface UserSubscription {
  id: number;
  plan: SubscriptionPlan;
  status: string;
  startDate: string;
  endDate: string | null;
  autoRenew: boolean;
  isActive: boolean;
}

export interface UsageStats {
  currentUsage: number;
  remainingUsage: number;
  maxUsage: number;
  isUnlimited: boolean;
  actionType: string;
}

export interface SubscriptionRequest {
  planId: number;
  paymentMethod?: string;
}

export const subscriptionApi = {
  getPlans: () => client.get('/subscriptions/plans'),

  getCurrentSubscription: () => client.get('/subscriptions/current'),

  getSubscriptionHistory: () => client.get('/subscriptions/history'),

  subscribe: (data: SubscriptionRequest) => client.post('/subscriptions/subscribe', data),

  cancelSubscription: (reason?: string) =>
    client.post('/subscriptions/cancel', null, { params: { reason } }),

  getUsageStats: (actionType: string, sessionId?: string) =>
    client.get(`/subscriptions/usage/${actionType}`, { params: { sessionId } }),
};

export default subscriptionApi;
