import React, { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { Check, Sparkles, Zap, Crown, ArrowLeft } from 'lucide-react';
import { Button, Loading } from '../../components/ui';
import { subscriptionApi } from '../../api';
import toast from 'react-hot-toast';

interface PricingPlan {
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

const PricingPage: React.FC = () => {
  const navigate = useNavigate();
  const [plans, setPlans] = useState<PricingPlan[]>([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    fetchPlans();
  }, []);

  const fetchPlans = async () => {
    try {
      const response = await subscriptionApi.getPlans();
      setPlans(response.data || []);
    } catch (error) {
      toast.error('요금제 정보를 불러오지 못했습니다.');
    } finally {
      setLoading(false);
    }
  };

  const getPlanIcon = (name: string) => {
    switch (name.toUpperCase()) {
      case 'FREE':
        return <Sparkles className="w-8 h-8" />;
      case 'PRO':
        return <Zap className="w-8 h-8" />;
      case 'PLATINUM':
        return <Crown className="w-8 h-8" />;
      default:
        return <Sparkles className="w-8 h-8" />;
    }
  };

  const getPlanGradient = (name: string) => {
    switch (name.toUpperCase()) {
      case 'FREE':
        return 'from-accent-blue to-primary-500';
      case 'PRO':
        return 'from-accent-purple to-accent-blue';
      case 'PLATINUM':
        return 'from-accent-yellow to-accent-red';
      default:
        return 'from-primary-400 to-primary-600';
    }
  };

  const getRecommendedPlan = (name: string) => {
    return name.toUpperCase() === 'PRO';
  };

  const formatPrice = (price: number) => {
    return new Intl.NumberFormat('ko-KR').format(price);
  };

  const handleSelectPlan = (plan: PricingPlan) => {
    if (plan.name.toUpperCase() === 'FREE') {
      navigate('/signup');
    } else {
      navigate('/login', { state: { selectedPlan: plan.id } });
    }
  };

  if (loading) {
    return (
      <div className="min-h-screen bg-gradient-dark flex items-center justify-center">
        <Loading size="lg" text="요금제 정보를 불러오는 중..." />
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-gradient-dark">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-12">
        {/* Header */}
        <div className="mb-8">
          <Button
            variant="outline"
            onClick={() => navigate('/')}
            className="border-dark-border-secondary text-dark-text-primary hover:bg-dark-bg-tertiary mb-6"
          >
            <ArrowLeft className="w-4 h-4 mr-2" />
            돌아가기
          </Button>
          <div className="text-center">
            <h1 className="text-4xl md:text-5xl font-bold text-dark-text-primary mb-4">
              성장에 맞는 <span className="text-gradient">요금제</span>를 선택하세요
            </h1>
            <p className="text-lg text-dark-text-secondary max-w-2xl mx-auto">
              무료 체험부터 전문가 플랜까지, 나에게 딱 맞는 플랜으로 시작하세요
            </p>
          </div>
        </div>

        {/* Pricing Cards */}
        <div className="grid md:grid-cols-3 gap-8 max-w-6xl mx-auto">
          {plans.map((plan, index) => {
            const isRecommended = getRecommendedPlan(plan.name);
            const gradient = getPlanGradient(plan.name);

            return (
              <div
                key={plan.id}
                className={`relative glass-card rounded-2xl p-8 transition-all duration-300 animate-slide-up ${
                  isRecommended
                    ? 'border-2 border-primary-500 shadow-glow scale-105'
                    : 'border border-dark-border-primary hover:border-dark-border-secondary'
                }`}
                style={{ animationDelay: `${index * 100}ms` }}
              >
                {/* Recommended Badge */}
                {isRecommended && (
                  <div className="absolute -top-4 left-1/2 transform -translate-x-1/2">
                    <div className="bg-gradient-to-r from-primary-500 to-accent-purple px-4 py-1 rounded-full text-sm font-semibold text-white shadow-glow">
                      추천
                    </div>
                  </div>
                )}

                {/* Icon */}
                <div className={`inline-flex p-4 rounded-xl bg-gradient-to-r ${gradient} bg-opacity-10 mb-4`}>
                  {getPlanIcon(plan.name)}
                </div>

                {/* Plan Name */}
                <h3 className="text-2xl font-bold text-dark-text-primary mb-2">
                  {plan.nameKo}
                </h3>
                <p className="text-sm text-dark-text-secondary mb-6">
                  {plan.description}
                </p>

                {/* Price */}
                <div className="mb-6">
                  <div className="flex items-baseline gap-2">
                    <span className="text-4xl font-bold text-dark-text-primary">
                      {plan.price === 0 ? '무료' : `₩${formatPrice(plan.price)}`}
                    </span>
                    {plan.price > 0 && (
                      <span className="text-dark-text-secondary">/월</span>
                    )}
                  </div>
                  <div className="text-sm text-dark-text-muted mt-1">
                    {plan.maxAnalysesPerMonth === -1
                      ? '무제한 분석'
                      : `월 ${plan.maxAnalysesPerMonth}회 분석`}
                  </div>
                </div>

                {/* Features */}
                <ul className="space-y-3 mb-8">
                  {plan.features.map((feature, featureIndex) => (
                    <li key={featureIndex} className="flex items-start gap-3">
                      <Check className="w-5 h-5 text-accent-green flex-shrink-0 mt-0.5" />
                      <span className="text-sm text-dark-text-secondary">{feature}</span>
                    </li>
                  ))}
                </ul>

                {/* CTA Button */}
                <Button
                  fullWidth
                  onClick={() => handleSelectPlan(plan)}
                  className={
                    isRecommended
                      ? 'bg-primary-500 hover:bg-primary-600 text-white shadow-glow'
                      : 'bg-dark-bg-elevated hover:bg-dark-bg-secondary text-dark-text-primary border border-dark-border-secondary'
                  }
                >
                  {plan.name.toUpperCase() === 'FREE' ? '무료로 시작하기' : '구독하기'}
                </Button>
              </div>
            );
          })}
        </div>

        {/* FAQ Section */}
        <div className="mt-24 max-w-3xl mx-auto">
          <h2 className="text-3xl font-bold text-dark-text-primary mb-8 text-center">
            자주 묻는 질문
          </h2>
          <div className="space-y-6">
            <div className="glass-card rounded-lg p-6">
              <h3 className="text-lg font-semibold text-dark-text-primary mb-2">
                무료 체험 후 자동으로 유료 전환되나요?
              </h3>
              <p className="text-dark-text-secondary">
                아니요. 무료 플랜은 영구적으로 제공되며, 유료 플랜으로의 전환은 사용자가 직접 선택해야 합니다.
              </p>
            </div>
            <div className="glass-card rounded-lg p-6">
              <h3 className="text-lg font-semibold text-dark-text-primary mb-2">
                언제든지 플랜을 변경할 수 있나요?
              </h3>
              <p className="text-dark-text-secondary">
                네, 언제든지 플랜을 업그레이드하거나 다운그레이드할 수 있습니다. 변경 사항은 즉시 적용됩니다.
              </p>
            </div>
            <div className="glass-card rounded-lg p-6">
              <h3 className="text-lg font-semibold text-dark-text-primary mb-2">
                환불 정책은 어떻게 되나요?
              </h3>
              <p className="text-dark-text-secondary">
                구독 후 7일 이내에는 100% 환불이 가능합니다. 그 이후에는 남은 기간에 대해 일할 계산하여 환불해드립니다.
              </p>
            </div>
          </div>
        </div>

        {/* Contact CTA */}
        <div className="mt-16 text-center">
          <p className="text-dark-text-secondary mb-4">
            더 궁금한 점이 있으신가요?
          </p>
          <Button
            variant="outline"
            className="border-dark-border-secondary text-dark-text-primary hover:bg-dark-bg-tertiary"
          >
            고객 지원팀에 문의하기
          </Button>
        </div>
      </div>
    </div>
  );
};

export default PricingPage;
