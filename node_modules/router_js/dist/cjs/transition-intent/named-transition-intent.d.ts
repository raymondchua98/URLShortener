import { Dict } from '../core';
import InternalRouteInfo, { Route, UnresolvedRouteInfoByObject, UnresolvedRouteInfoByParam } from '../route-info';
import Router, { ParsedHandler } from '../router';
import { TransitionIntent } from '../transition-intent';
import TransitionState from '../transition-state';
export default class NamedTransitionIntent<T extends Route> extends TransitionIntent<T> {
    name: string;
    pivotHandler?: Route;
    contexts: unknown[];
    queryParams: Dict<unknown>;
    preTransitionState?: TransitionState<T>;
    constructor(router: Router<T>, name: string, pivotHandler: Route | undefined, contexts?: unknown[], queryParams?: Dict<unknown>, data?: {});
    applyToState(oldState: TransitionState<T>, isIntermediate: boolean): TransitionState<T>;
    applyToHandlers(oldState: TransitionState<T>, parsedHandlers: ParsedHandler[], targetRouteName: string, isIntermediate: boolean, checkingIfActive: boolean): TransitionState<T>;
    invalidateChildren(handlerInfos: InternalRouteInfo<T>[], invalidateIndex: number): void;
    getHandlerInfoForDynamicSegment(name: string, names: string[], objects: unknown[], oldHandlerInfo: InternalRouteInfo<T>, _targetRouteName: string, i: number): InternalRouteInfo<T> | UnresolvedRouteInfoByObject<T>;
    createParamHandlerInfo(name: string, names: string[], objects: unknown[], oldHandlerInfo: InternalRouteInfo<T>): UnresolvedRouteInfoByParam<T>;
}
